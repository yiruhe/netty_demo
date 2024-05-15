package com.example.cacfdemo.client;

/**
 * @CreateTime: 2024-05-15
 * @Description: TODO
 * @Version: 1.0
 */
public class RunnableWrapper implements Runnable {
    // 实际要执行的线程任务
    private Runnable task;
    // 线程任务被创建出来的时间
    private long createTime;
    // 线程任务被线程池运行的开始时间
    private long startTime;
    // 线程任务被线程池运行的结束时间
    private long endTime;
    // 线程信息
    private String taskInfo;

    private boolean showWaitLog;

    /**
     * 执行间隔时间多久，打印日志
     */
    private long durMs = 1000L;

    // 当这个任务被创建出来的时候，就会设置他的创建时间
    // 但是接下来有可能这个任务提交到线程池后，会进入线程池的队列排队
    public RunnableWrapper(Runnable task, String taskInfo) {
        this.task = task;
        this.taskInfo = taskInfo;
        this.createTime = System.currentTimeMillis();
    }

    public void setShowWaitLog(boolean showWaitLog) {
        this.showWaitLog = showWaitLog;
    }

    public void setDurMs(long durMs) {
        this.durMs = durMs;
    }

    // 当任务在线程池排队的时候，这个run方法是不会被运行的
    // 但是当任务结束了排队，得到线程池运行机会的时候，这个方法会被调用
    // 此时就可以设置线程任务的开始运行时间
    @Override
    public void run() {
        this.startTime = System.currentTimeMillis();

        // 此处可以通过调用监控系统的API，实现监控指标上报
        // 用线程任务的startTime-createTime，其实就是任务排队时间
        // 这边打印日志输出，也可以输出到监控系统中
        if(showWaitLog) {
            //log.info("任务信息: [{}], 任务排队时间: [{}]ms", taskInfo, startTime - createTime);
        }

        // 接着可以调用包装的实际任务的run方法
        try {
            task.run();
        } catch (Exception e) {
           // log.error("run task error", e);
        }

        // 任务运行完毕以后，会设置任务运行结束的时间
        this.endTime = System.currentTimeMillis();

        // 此处可以通过调用监控系统的API，实现监控指标上报
        // 用线程任务的endTime - startTime，其实就是任务运行时间
        // 这边打印任务执行时间，也可以输出到监控系统中
        if(endTime - startTime > durMs) {
            //log.info("任务信息: [{}], 任务执行时间: [{}]ms", taskInfo, endTime - startTime);
        }

    }
}
