package com.thoughtworks.rslist.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RsEvent implements Serializable , Comparable<RsEvent>{
    @NotNull
    private String eventName;
    @NotNull
    private String keyword;
    private int voteNum;
    private int rank;
    @NotNull
    private int userId;


    @Override
    public int compareTo(RsEvent rsEvent) {
        return new Integer((rsEvent.getVoteNum() - this.voteNum)).intValue();
    }
}
