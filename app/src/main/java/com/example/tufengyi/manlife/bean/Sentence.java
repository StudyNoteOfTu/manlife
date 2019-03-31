package com.example.tufengyi.manlife.bean;

public class Sentence {
    public long id;
    public String sentence;

    public Sentence(){}

    public Sentence(String sentence){
        super();
        this.sentence = sentence;
    }

    public Sentence(long id, String sentence){
        super();
        this.sentence = sentence;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }
}
