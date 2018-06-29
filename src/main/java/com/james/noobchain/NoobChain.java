package com.james.noobchain;

import com.james.blockchain.Block;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

public class NoobChain {

    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();
    public static int difficulty = 5;
    public static float minimumTransaction = 0.1f;
    public static Wallet walletA;
    public static Wallet walletB;
    public static Transaction genesisTransaction;

    //验证区块是否有效
    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        HashMap<String,TransactionOutput> tempUTXOs = new HashMap<String,TransactionOutput>(); //在给定块状态下未使用的事务的临时工作清单。
        tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        //通过区块链循环检查散列
        for(int i=1; i < blockchain.size(); i++) {

            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);
            //比较注册的散列和计算的散列
            if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
                System.out.println("#Current Hashes not equal");
                return false;
            }
            //比较以前的散列和注册的前一个散列
            if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
                System.out.println("#Previous Hashes not equal");
                return false;
            }
            //检查散列是否解决
            if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
                System.out.println("#This block hasn't been mined");
                return false;
            }

            //通过区块链交易循环
            TransactionOutput tempOutput;
            for(int t=0; t <currentBlock.transactions.size(); t++) {
                Transaction currentTransaction = currentBlock.transactions.get(t);

                if(!currentTransaction.verifiySignature()) {
                    System.out.println("#Signature on Transaction(" + t + ") is Invalid");
                    return false;
                }
                if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
                    System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
                    return false;
                }

                for(TransactionInput input: currentTransaction.inputs) {
                    tempOutput = tempUTXOs.get(input.transactionOutputId);

                    if(tempOutput == null) {
                        System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
                        return false;
                    }

                    if(input.UTXO.value != tempOutput.value) {
                        System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
                        return false;
                    }

                    tempUTXOs.remove(input.transactionOutputId);
                }

                for(TransactionOutput output: currentTransaction.outputs) {
                    tempUTXOs.put(output.id, output);
                }

                if( currentTransaction.outputs.get(0).reciepient != currentTransaction.reciepient) {
                    System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
                    return false;
                }
                if( currentTransaction.outputs.get(1).reciepient != currentTransaction.sender) {
                    System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
                    return false;
                }

            }

        }
        System.out.println("区块链是有效的!!!");
        return true;
    }
    //创建区块
    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }
    public static void main(String[] args) {
        //设置BouncyCastle作为安全提供商
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        //创建多个钱包
        walletA = new Wallet();
        walletB = new Wallet();
        Wallet coinbase = new Wallet();

        //创建发生交易，发送100 Noob钱币到walletA：
        genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
        genesisTransaction.generateSignature(coinbase.privateKey);	 //手动签署交易
        genesisTransaction.transactionId = "0"; //手动设置交易ID
        genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciepient, genesisTransaction.value, genesisTransaction.transactionId)); //手动添加事务输出
        UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); //将我们的第一笔交易存储在UTXOs列表中。

        System.out.println("创建和挖掘块... ");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        //测试
        Block block1 = new Block(genesis.hash);
        System.out.println("WalletA的余额是: " + walletA.getBalance());
        System.out.println("walletA正在尝试将资金（40）发送到walletB...");
        block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
        addBlock(block1);
        System.out.println("WalletA的余额是: " + walletA.getBalance());
        System.out.println("WalletB的余额是: " + walletB.getBalance());

        Block block2 = new Block(block1.hash);
        System.out.println("WalletA 正在尝试发送将资金（1000）发送...");
        block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
        addBlock(block2);
        System.out.println("WalletA的余额是: " + walletA.getBalance());
        System.out.println("WalletB的余额是: " + walletB.getBalance());

        Block block3 = new Block(block2.hash);
        System.out.println("WalletB正在尝试将资金（20）发送到walletA...");
        block3.addTransaction(walletB.sendFunds( walletA.publicKey, 20));
        System.out.println("WalletA的余额是: " + walletA.getBalance());
        System.out.println("WalletB的余额是: " + walletB.getBalance());

        isChainValid();
        System.out.println(blockchain);
    }

}
