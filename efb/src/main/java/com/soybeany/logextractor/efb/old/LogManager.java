//package com.soybeany.log;
//
//import old.DataException;
//import com.soybeany.log.old.data.ExtractData;
//import com.soybeany.log.handler.BaseHandler;
//import com.soybeany.log.handler.HandlerFactory;
//import com.soybeany.log.loader.ILoader;
//import com.soybeany.log.loader.LoaderFactory;
//import com.soybeany.log.parser.IParser;
//import com.soybeany.log.parser.ParserFactory;
//import com.soybeany.log.reporter.IReporter;
//import com.soybeany.log.reporter.ReporterFactory;
//
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.concurrent.Executor;
//import java.util.concurrent.Executors;
//
///**
// * <br>Created by Soybeany on 2020/2/4.
// */
//class LogManager {
//
//    // ****************************************常量定义****************************************
//
//    /**
//     * 同步等待 成功
//     */
//    static final String CODE_SYNC_SUCCESS = "sync_success";
//
//    /**
//     * 同步等待 失败
//     */
//    static final String CODE_SYNC_FAILURE = "sync_failure";
//
//    /**
//     * 异步处理 成功
//     */
//    static final String CODE_ASYNC_SUCCESS = "async_success";
//
//    /**
//     * 异步处理 失败
//     */
//    static final String CODE_ASYNC_FAILURE = "async_failure";
//
//    /**
//     * 查询结果 成功
//     */
//    static final String CODE_QUERY_SUCCESS = "query_success";
//
//    /**
//     * 查询结果 未完成
//     */
//    static final String CODE_QUERY_PROCESSING = "query_processing";
//
//    /**
//     * 查询结果 失败
//     */
//    static final String CODE_QUERY_FAILURE = "query_failure";
//
//    /**
//     * 参数异常
//     */
//    static final String CODE_PARAM_EXCEPTION = "data_exception";
//
//    // ****************************************成员变量****************************************
//
//    private static final Set<String> MODE_CONTAINER = new HashSet<String>(Arrays.asList(ExtractData.MODE_SYNC, ExtractData.MODE_ASYNC, ExtractData.MODE_QUERY));
//    private static final Executor EXECUTOR = Executors.newCachedThreadPool();
//
//    // ****************************************公开的API****************************************
//
//    static Result getResult(ExtractData data) {
//        return innerGetResult(data);
//    }
//
//    // ****************************************内部方法****************************************
//
//    private static Result innerGetResult(ExtractData eData) {
//        // 创建处理类
//        try {
//            ILoader loader = LoaderFactory.get(eData.loader);
//            IParser parser = ParserFactory.get(eData.parser);
//            List<BaseHandler> handlers = HandlerFactory.get(eData.handler);
//            IReporter reporter = ReporterFactory.get(eData.reporter);
//            // 加载文件
//            String nextLine = loader.getNextLine();
//            while (null != nextLine) {
//
//                nextLine = loader.getNextLine();
//            }
//            return Result.norm()
//        } catch (DataException e) {
//            return Result.error(CODE_PARAM_EXCEPTION, e);
//        }
//    }
//
//    // ****************************************内部类定义****************************************
//
//    static class Result {
//        String taskId;
//        String code;
//        String data;
//        Exception e;
//
//        static Result norm(String code, String data) {
//            return new Result(code, data, null);
//        }
//
//        static Result error(String code, Exception e) {
//            return new Result(code, null, e);
//        }
//
//        private Result(String code, String data, Exception e) {
//            this.code = code;
//            this.data = data;
//            this.e = e;
//        }
//    }
//}
