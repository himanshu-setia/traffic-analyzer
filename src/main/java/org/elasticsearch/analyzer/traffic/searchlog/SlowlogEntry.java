package org.elasticsearch.analyzer.traffic.searchlog;


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
public class SlowlogEntry {

    private DateTime timestamp = null;
    private String phase ;
    private String nodename;
    private String indexname;
    private int shard;
    private long tookmillis;
    private long totalhits;
    private int totalshards;
    private String source;
    private String hashcode;
    private String searchtype;

    public String toJson() {
        try {
            return Strings.toString(
                    XContentFactory.jsonBuilder()
                            .startObject()
                            .field("timestamp", timestamp.toString())
                            .field("phase", phase)
                            .field("nodename", nodename)
                            .field("indexname", indexname)
                            .field("shard", shard)
                            .field("tookmillis", tookmillis)
                            .field("totalhits", totalhits)
                            .field("totalshards", totalshards)
                            .field("source", source)
                            .field("hashcode", hashcode)
                            .field("searchtype", searchtype)
                            .endObject()
            );
        }catch (IOException ex){
            return null;
        }
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    public SlowlogEntry setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public String getPhase() {
        return phase;
    }

    public SlowlogEntry setPhase(String phase) {
        this.phase = phase;
        return this;
    }

    public String getNodename() {
        return nodename;
    }

    public SlowlogEntry setNodename(String nodename) {
        this.nodename = nodename;
        return this;
    }

    public String getIndexname() {
        return indexname;
    }

    public SlowlogEntry setIndexname(String indexname) {
        this.indexname = indexname;
        return this;
    }

    public int getShard() {
        return shard;
    }

    public SlowlogEntry setShard(int shard) {
        this.shard = shard;
        return this;
    }

    public long getTookmillis() {
        return tookmillis;
    }

    public SlowlogEntry setTookmillis(long tookmillis) {
        this.tookmillis = tookmillis;
        return this;
    }

    public long getTotalhits() {
        return totalhits;
    }

    public SlowlogEntry setTotalhits(long totalhits) {
        this.totalhits = totalhits;
        return this;
    }

    public int getTotalshards() {
        return totalshards;
    }

    public SlowlogEntry setTotalshards(int totalshards) {
        this.totalshards = totalshards;
        return this;
    }

    public String getSource() {
        return source;
    }

    public SlowlogEntry setSource(String source) {
        this.source = source;
        return this;
    }

    public SlowlogEntry setHashcode(String hash) {
        this.hashcode = hash;
        return this;
    }

    public SlowlogEntry setSearchType(String searchType) {
        this.searchtype = searchtype;
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
