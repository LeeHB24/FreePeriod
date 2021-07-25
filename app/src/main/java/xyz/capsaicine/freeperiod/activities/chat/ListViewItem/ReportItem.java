package xyz.capsaicine.freeperiod.activities.chat.ListViewItem;

public class ReportItem {
    private int partyId;
    private int report_writer;
    private int report_target;
    private String content;
    private String timestamp;

    public ReportItem(int partyId, int report_writer, int report_target, String content, String timestamp) {
        this.partyId = partyId;
        this.report_writer = report_writer;
        this.report_target = report_target;
        this.content = content;
        this.timestamp = timestamp;
    }

    public ReportItem() {
    }

    public int getPartyId() {
        return partyId;
    }

    public void setPartyId(int partyId) {
        this.partyId = partyId;
    }

    public int getReport_writer() {
        return report_writer;
    }

    public void setReport_writer(int report_writer) {
        this.report_writer = report_writer;
    }

    public int getReport_target() {
        return report_target;
    }

    public void setReport_target(int report_target) {
        this.report_target = report_target;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
