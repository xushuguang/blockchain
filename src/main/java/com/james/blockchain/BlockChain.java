package com.james.blockchain;

import java.util.ArrayList;
import java.util.Date;

/**
 * 创建区块链
 */
public class BlockChain {
    public static ArrayList<Block> blockchain = new ArrayList<>();//创建用于存放所有区块的集合
    public static int difficulty = 5;//挖矿的难度，数字越大难度越大

    /**
     * 检查区块链的完整性
     * @return boolean
     */
    public static boolean isChainValid(){
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        //循环检查区块链的散列
        for (int i=1;i<blockchain.size();i++){
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);
            //比较注册散列和计算散列
            if (!currentBlock.hash.equals(currentBlock.calculateHash())){
                System.out.println("Current Hashes not equal");
                return false;
            }
            //比较以前的散列和注册的先前的散列
            if (!previousBlock.hash.equals(currentBlock.previousHash)){
                System.out.println("Previous Hashes not equal");
                return false;
            }
            //检查hash值是否已经被使用
            if (!currentBlock.hash.substring(0,difficulty).equals(hashTarget)){
                System.out.println("这个区块还没有被开采。。。");
                return false;
            }
        }
        return true;
    }
    public static void addBlock(Block newBlock){
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }
    public static void main(String[] args){
        System.out.println("正在创建第1个区块....... "+new Date());
        addBlock(new Block("0"));//创世块
        System.out.println("第1个区块创建完成....... "+new Date());
        for (int i = 2;i<10;i++){
            System.out.println("正在创建第"+i+"个区块....... "+new Date());
            addBlock(new Block( blockchain.get(blockchain.size()-1).hash));
            System.out.println("第"+i+"个区块创建完成....... "+new Date());
        }
        System.out.println("区块链是否有效的: " + isChainValid());
        String blockchainJson = StringUtil.getJSON(blockchain);
        System.out.println(blockchainJson);
    }
}
