package com.matou.smartcar.event;


/**
 * 升级事件
 * @author ranfeng
 */
public class UpgradeEvent {

    /**
     * 升级状态
     * 2：检查更新-无升级
     * 1：升级中，progress有效
     * 0：升级包下载成功
     * -1：用户取消升级
     * -2：升级失败，网络异常
     * -3：升级失败，其他原因
     */
    private int status;

    /**
     * 升级进度
     */
    private int progress;


    public UpgradeEvent(int status, int progress) {
        this.status = status;
        this.progress = progress;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public String toString() {
        return "UpgradeEvent{" +
                "status=" + status +
                ", progress=" + progress +
                '}';
    }
}
