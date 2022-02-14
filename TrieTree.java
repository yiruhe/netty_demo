package com.example.demo.trie;

import java.util.HashMap;

public class TrieTree {
    private TrieNode root;


    public TrieTree() {
        root = new TrieNode();
    }


    public void insert(String word){
        TrieNode currentNode = this.root;
        for (int i = 0; i < word.length(); i++) {
            char chr = word.charAt(i);
            if(!currentNode.map.containsKey(chr)){
                currentNode.map.put(chr,new TrieNode(chr));
            }
            //获取下一级
            currentNode = currentNode.map.get(chr);

            //是否结束
            if(i==word.length()-1){
                currentNode.isWord = true;
            }
        }

    }



    public boolean search(String word){

        TrieNode cur = root;
        for(int i = 0; i < word.length(); i++){
            char chr = word.charAt(i);
            if(!cur.map.containsKey(chr)) return false;
            cur = cur.map.get(chr);
        }
        //是终点返回true;
        return cur.isWord;
    }


    public boolean startsWith(String prefix) {
        TrieNode cur = root;
        for(int i = 0; i < prefix.length(); i++){
            char chr = prefix.charAt(i);
            if(!cur.map.containsKey(chr)) return false;
            cur = cur.map.get(chr);
        }
        return true;
    }

}


class TrieNode {
    // Initialize your data structure here.
    Character val;
    HashMap<Character, TrieNode> map;
    boolean isWord;
    public TrieNode() {
        val = null;
        map = new HashMap<Character, TrieNode>();
        isWord = false;
    }

    public TrieNode(char chr){
        val = chr;
        map = new HashMap<Character, TrieNode>();
        isWord = false;
    }
}

