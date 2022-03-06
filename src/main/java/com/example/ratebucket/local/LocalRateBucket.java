package com.example.ratebucket.local;

public class LocalRateBucket extends AbstractRateBucket {

    @Override
    public RateBucket buildRateBucket(int limitPerSecond) {
        
        getTimeMeter();



        return null;
    }



    @Override
    public boolean tryConsume(int tokens) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void tryConsumeWithBlock() {
        // TODO Auto-generated method stub
        
    }


}
