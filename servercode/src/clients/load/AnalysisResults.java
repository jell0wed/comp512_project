package clients.load;

public class AnalysisResults {
    int requestCount;
    int transactionCount;
    int earlyAbort;
    int transactionError;
    long lowestReponseTime;
    long highestReponseTime;
    int stopRequestCount;

    public void appendResponseTime(long delta) {
        this.lowestReponseTime = Long.min(this.lowestReponseTime, delta);
        this.highestReponseTime = Long.max(this.highestReponseTime, delta);
    }
}
