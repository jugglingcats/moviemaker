package com.akirkpatrick.mm.web;

import java.util.ArrayList;
import java.util.UUID;

public class MovieMakerSession {
    ArrayList<UUID> frames=new ArrayList<UUID>();

    public void addFrame(UUID uuid) {
        frames.add(uuid);
    }

    public ArrayList<UUID> getFrames() {
        return frames;
    }
}
