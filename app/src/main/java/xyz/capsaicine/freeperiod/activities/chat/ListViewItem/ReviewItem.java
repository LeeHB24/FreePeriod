package xyz.capsaicine.freeperiod.activities.chat.ListViewItem;

public class ReviewItem {
    private int reviewIndex;
    private float rating;
    private String content;
    private int reviewWriter;
    private int reviewTarget;
    private String timestamp;
    private int partyId;

    public ReviewItem(int reviewIndex, String content, float rating, String timestamp)
    {
        this.reviewIndex=reviewIndex;
        this.content = content;
        this.rating = rating;
        this.timestamp = timestamp;
    }

    public ReviewItem(int reviewWriter, int reviewTarget, int partyId, String content, float rating, String timestamp){
        this.reviewWriter = reviewWriter;
        this.reviewTarget = reviewTarget;
        this.partyId = partyId;
        this.content = content;
        this.rating = rating;
        this.timestamp = timestamp;
    }

    public int getReviewIndex() {
        return reviewIndex;
    }

    public void setReviewIndex(int reviewIndex) {
        this.reviewIndex = reviewIndex;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
