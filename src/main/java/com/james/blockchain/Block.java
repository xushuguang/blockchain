package com.james.blockchain;

import com.james.noobchain.Transaction;

import java.util.ArrayList;
import java.util.Date;

public class Block {
    //这个区块的hash值
    public String hash;
    //上一个区块的hash值
    public String previousHash;
    public String merkleRoot;
    //每个区块存放的信息
    public ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    //时间戳
    private long timeStamp;
    //挖矿者的工作量证明
    private int nonce;

    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        //根据previousHash、data和timeStamp产生唯一hash
        this.hash = calculateHash();
    }
    //基于上一块的内容计算新的散列
    public String calculateHash() {
        String calculatedHash = StringUtil.applySha256(
                previousHash +
                        Long.toString(timeStamp) +
                        Integer.toString(nonce) +
                        merkleRoot
        );
        return calculatedHash;
    }
    //挖矿
    public void mineBlock(int difficulty) {
        //目标值，difficulty越大，下面计算量越大
        String target = StringUtil.getDificultyString(difficulty);
        //difficulty如果为5，那么target则为 00000
        while(!hash.substring( 0, difficulty).equals(target)) {
            nonce ++;
            hash = calculateHash();
        }
        System.out.println("创建区块:" + hash);
    }
    //将交易添加到此块
    public boolean addTransaction(Transaction transaction) {
        //处理事务并检查是否有效，除非块是生成块然后忽略。
        if(transaction == null) return false;
        if((previousHash != "0")) {
            if((transaction.processTransaction() != true)) {
                System.out.println("交易未能处理。丢弃。");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("交易成功添加到Block");
        return true;
    }

    @Override
    public String toString() {
        return "Block{" +
                "hash='" + hash + '\'' +
                ", previousHash='" + previousHash + '\'' +
                ", merkleRoot='" + merkleRoot + '\'' +
                ", transactions=" + transactions +
                ", timeStamp=" + timeStamp +
                ", nonce=" + nonce +
                '}';
    }
}
