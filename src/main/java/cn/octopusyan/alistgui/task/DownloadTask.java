package cn.octopusyan.alistgui.task;

import cn.octopusyan.alistgui.base.BaseTask;
import cn.octopusyan.alistgui.config.Constants;
import cn.octopusyan.alistgui.manager.http.HttpUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * TODO 下载任务
 *
 * @author octopus_yan
 */
@Slf4j
public class DownloadTask extends BaseTask {
    private final String downloadUrl;

    public DownloadTask(String downloadUrl) {
        this.downloadUrl = downloadUrl;
        log.info(STR."downlaod url : \{downloadUrl}");
    }

    @Override
    protected void task() throws Exception {
        HttpClient client = HttpUtil.getInstance().getHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(downloadUrl)).build();

        // 检查bin目录
        File binDir = new File(Constants.BIN_DIR_PATH);
        if (!binDir.exists()) {
            log.debug(STR."dir [\{Constants.BIN_DIR_PATH}] not exists");
            //noinspection ResultOfMethodCallIgnored
            binDir.mkdirs();
            log.debug(STR."created dir [\{Constants.BIN_DIR_PATH}]");
        }

        // 下载处理器
        var handler = BodyHandler.create(
                Path.of(Constants.BIN_DIR_PATH),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE
        );

        // 下载监听
        handler.listener((total, progress) -> {
            // 输出进度
            if (listener != null && listener instanceof Listener lis)
                lis.onProgress(total, progress);
        });

        log.info("download start");
        HttpResponse<Path> response = client.send(request, handler);
        log.info("download success");
    }

    public interface Listener extends BaseTask.Listener {
        void onProgress(Long total, Long progress);
    }

    /**
     * 自定义下载返回处理
     */
    private static class BodyHandler implements HttpResponse.BodyHandler<Path> {
        private final HttpResponse.BodyHandler<Path> handler;
        private BiConsumer<Long, Long> consumer;

        private BodyHandler(HttpResponse.BodyHandler<Path> handler) {
            this.handler = handler;
        }

        public static BodyHandler create(Path directory, OpenOption... openOptions) {
            return new BodyHandler(HttpResponse.BodyHandlers.ofFileDownload(directory, openOptions));
        }

        @Override
        public HttpResponse.BodySubscriber<Path> apply(HttpResponse.ResponseInfo responseInfo) {
            AtomicLong length = new AtomicLong(-1);
            // 获取文件大小
            Optional<String> string = responseInfo.headers().firstValue("content-length");
            string.ifPresentOrElse(s -> {
                length.set(Long.parseLong(s));
                log.debug(STR."========={content-length = \{s}}=========");
            }, () -> {
                String msg = "response not has header [content-length]";
                log.error(msg);
            });

            BodySubscriber subscriber = new BodySubscriber(handler.apply(responseInfo));
            subscriber.setConsumer(progress -> consumer.accept(length.get(), progress));

            return subscriber;
        }

        public void listener(BiConsumer<Long, Long> consumer) {
            this.consumer = consumer;
        }
    }

    public static class BodySubscriber implements HttpResponse.BodySubscriber<Path> {
        private final HttpResponse.BodySubscriber<Path> subscriber;
        private final AtomicLong progress = new AtomicLong(0);
        @Setter
        private Consumer<Long> consumer;

        public BodySubscriber(HttpResponse.BodySubscriber<Path> subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public CompletionStage<Path> getBody() {
            return subscriber.getBody();
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            subscriber.onSubscribe(subscription);
        }

        @Override
        public void onNext(List<ByteBuffer> item) {
            subscriber.onNext(item);

            // 记录进度
            for (ByteBuffer byteBuffer : item) {
                progress.addAndGet(byteBuffer.limit());
            }
            consumer.accept(progress.get());
        }

        @Override
        public void onError(Throwable throwable) {
            subscriber.onError(throwable);
        }

        @Override
        public void onComplete() {
            subscriber.onComplete();

            consumer.accept(progress.get());
        }
    }
}
