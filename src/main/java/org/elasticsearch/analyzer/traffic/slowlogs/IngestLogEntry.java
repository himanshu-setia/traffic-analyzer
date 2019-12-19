package org.elasticsearch.analyzer.traffic.slowlogs;


import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.joda.time.DateTime;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This object is stored as an entry in the slow-log-index.
 */
public class IngestLogEntry {

    private DateTime timestamp = null;
    private String nodename;
    private String indexname;
    private int shard;
    private long tookmillis;

    public String toJson() {
        try {
            return Strings.toString(
                    XContentFactory.jsonBuilder()
                            .startObject()
                            .field("timestamp", timestamp.toString())
                            .field("nodename", nodename)
                            .field("indexname", indexname)
                            .field("shard", shard)
                            .field("tookmillis", tookmillis)
                            .endObject()
            );
        }catch (IOException ex){
            return null;
        }
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    public IngestLogEntry setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public String getNodename() {
        return nodename;
    }

    public IngestLogEntry setNodename(String nodename) {
        this.nodename = nodename;
        return this;
    }

    public String getIndexname() {
        return indexname;
    }

    public IngestLogEntry setIndexname(String indexname) {
        this.indexname = indexname;
        return this;
    }

    public int getShard() {
        return shard;
    }

    public IngestLogEntry setShard(int shard) {
        this.shard = shard;
        return this;
    }

    public long getTookmillis() {
        return tookmillis;
    }

    public IngestLogEntry setTookmillis(long tookmillis) {
        this.tookmillis = tookmillis;
        return this;
    }

    private static MessageDigest messageDigest = null;
    static {
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex){
            //ignore.
        }
    }
    public static String generateHashcode(String string){
        return DatatypeConverter.printHexBinary(messageDigest.digest(string.getBytes()));
    }
}
