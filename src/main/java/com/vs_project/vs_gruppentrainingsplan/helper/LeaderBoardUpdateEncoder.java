package com.vs_project.vs_gruppentrainingsplan.helper;

import jakarta.websocket.EncodeException;
import jakarta.websocket.Encoder;
import org.json.JSONObject;

public class LeaderBoardUpdateEncoder implements Encoder.Text<LeaderBoardUpdateDTO> {
    @Override
    public String encode(LeaderBoardUpdateDTO leaderBoardUpdateDTO) throws EncodeException {
        return new JSONObject(leaderBoardUpdateDTO).toString();
    }
}
