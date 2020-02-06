//package com.soybeany.log;
//
//import old.DataException;
//import com.soybeany.log.old.data.ExtractData;
//
///**
// * 日志提取器(入口类)
// *
// * <br>loader:加载文件
// * <br>parser:解析文本结构，转换为结构化数据
// * <br>handler:处理结构化后的数据
// * <br>reporter:将数据以指定方式汇报
// *
// * <br>Created by Soybeany on 2020/2/4.
// */
//public class LogExtractor {
//
//    private static IHintProvider HINT_PROVIDER = new DefaultHintProvider();
//    private static IExceptionListener EXCEPTION_LISTENER = new DefaultExceptionListener();
//
//    // ****************************************公开的API****************************************
//
//    public static void setHintProvider(IHintProvider provider) {
//        if (null == provider) {
//            throw new RuntimeException("提示语提供者不能设置为null");
//        }
//        HINT_PROVIDER = provider;
//    }
//
//    public static void setExceptionListener(IExceptionListener listener) {
//        if (null == listener) {
//            throw new RuntimeException("异常监听器不能设置为null");
//        }
//        EXCEPTION_LISTENER = listener;
//    }
//
//    public static Result getResult(ExtractData data) {
//        try {
//            return innerGetResult(data);
//        } catch (Exception e) {
//            EXCEPTION_LISTENER.onDefault(e);
//            return Result.error(HINT_PROVIDER.getUnexpectException(e));
//        }
//    }
//
//    // ****************************************内部方法****************************************
//
//    private static Result innerGetResult(ExtractData data) throws Exception {
//        LogManager.Result result = LogManager.getResult(data);
//        // 正常系
//        if (LogManager.CODE_SYNC_SUCCESS.equals(result.code)) {
//            return Result.norm(result.data, HINT_PROVIDER.getSyncSuccessHint());
//        }
//        if (LogManager.CODE_ASYNC_SUCCESS.equals(result.code)) {
//            return Result.norm(result.data, HINT_PROVIDER.getAsyncSuccessHint());
//        }
//        if (LogManager.CODE_QUERY_SUCCESS.equals(result.code)) {
//            return Result.norm(result.data, HINT_PROVIDER.getQuerySuccessHint());
//        }
//        if (LogManager.CODE_QUERY_PROCESSING.equals(result.code)) {
//            return Result.norm(result.data, HINT_PROVIDER.getQueryProcessingHint());
//        }
//        // 参数异常
//        if (LogManager.CODE_PARAM_EXCEPTION.equals(result.code)) {
//            DataException e = (DataException) result.e;
//            EXCEPTION_LISTENER.onData(e);
//            return Result.error(HINT_PROVIDER.getDataExceptionHint(e));
//        }
//        // 异常系
//        if (LogManager.CODE_SYNC_FAILURE.equals(result.code)) {
//            return Result.error(HINT_PROVIDER.getSyncFailureHint(result.e));
//        }
//        if (LogManager.CODE_ASYNC_FAILURE.equals(result.code)) {
//            return Result.error(HINT_PROVIDER.getAsyncFailureHint(result.e));
//        }
//        if (LogManager.CODE_QUERY_FAILURE.equals(result.code)) {
//            return Result.error(HINT_PROVIDER.getQueryFailureHint(result.e));
//        }
//        // 状态码未知
//        throw new Exception("LogManager中使用了未知的状态码");
//    }
//
//    // ****************************************内部类定义****************************************
//
//    public interface IExceptionListener {
//        void onData(DataException e);
//
//        void onDefault(Exception e);
//    }
//
//    public interface IHintProvider {
//        String getSyncSuccessHint();
//
//        String getSyncFailureHint(Exception e);
//
//        String getAsyncSuccessHint();
//
//        String getAsyncFailureHint(Exception e);
//
//        String getQuerySuccessHint();
//
//        String getQueryProcessingHint();
//
//        String getQueryFailureHint(Exception e);
//
//        String getDataExceptionHint(DataException e);
//
//        String getUnexpectException(Exception e);
//    }
//
//    public static class Result {
//        public boolean isNorm;
//        public String taskId;
//        public String data;
//        public String msg;
//
//        static Result norm(String data, String msg) {
//            return new Result(true, data, msg);
//        }
//
//        static Result error(String msg) {
//            return new Result(false, null, msg);
//        }
//
//        private Result(boolean isNorm, String data, String msg) {
//            this.isNorm = isNorm;
//            this.data = data;
//            this.msg = msg;
//        }
//    }
//
//    // ****************************************内部默认实现类****************************************
//
//    private static class DefaultExceptionListener implements IExceptionListener {
//        public void onData(DataException e) {
//            System.out.println("参数异常：" + e.getMessage());
//        }
//
//        public void onDefault(Exception e) {
//            System.out.println("出现未预料的异常");
//            e.printStackTrace();
//        }
//    }
//
//    private static class DefaultHintProvider implements IHintProvider {
//
//        public String getSyncSuccessHint() {
//            return "任务执行成功";
//        }
//
//        public String getSyncFailureHint(Exception e) {
//            return "任务执行异常:" + e.getMessage();
//        }
//
//        public String getAsyncSuccessHint() {
//            return "任务已添加到队列";
//        }
//
//        public String getAsyncFailureHint(Exception e) {
//            return "任务添加异常:" + e.getMessage();
//        }
//
//        public String getQuerySuccessHint() {
//            return "任务查询成功";
//        }
//
//        public String getQueryProcessingHint() {
//            return "任务仍在后台处理中";
//        }
//
//        public String getQueryFailureHint(Exception e) {
//            return "任务查询异常:" + e.getMessage();
//        }
//
//        public String getDataExceptionHint(DataException e) {
//            return "参数异常:" + e.getMessage();
//        }
//
//        public String getUnexpectException(Exception e) {
//            return "未预料的异常:" + e.getMessage();
//        }
//    }
//}
