package com.thoughtworks.rslist.service;

import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.Trade;
import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.dto.RsEventDto;
import com.thoughtworks.rslist.dto.TradeDto;
import com.thoughtworks.rslist.dto.UserDto;
import com.thoughtworks.rslist.dto.VoteDto;
import com.thoughtworks.rslist.exception.IsBadRequestException;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.TradeRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RsService {
  final RsEventRepository rsEventRepository;
  final UserRepository userRepository;
  final VoteRepository voteRepository;
  final TradeRepository tradeRepository;

  public RsService(RsEventRepository rsEventRepository, UserRepository userRepository, VoteRepository voteRepository, TradeRepository tradeRepository) {
    this.rsEventRepository = rsEventRepository;
    this.userRepository = userRepository;
    this.voteRepository = voteRepository;
    this.tradeRepository = tradeRepository;
  }

  public void vote(Vote vote, int rsEventId) {
    Optional<RsEventDto> rsEventDto = rsEventRepository.findById(rsEventId);
    Optional<UserDto> userDto = userRepository.findById(vote.getUserId());
    if (!rsEventDto.isPresent()
        || !userDto.isPresent()
        || vote.getVoteNum() > userDto.get().getVoteNum()) {
      throw new RuntimeException();
    }
    VoteDto voteDto =
        VoteDto.builder()
            .localDateTime(vote.getTime())
            .num(vote.getVoteNum())
            .rsEvent(rsEventDto.get())
            .user(userDto.get())
            .build();
    voteRepository.save(voteDto);
    UserDto user = userDto.get();
    user.setVoteNum(user.getVoteNum() - vote.getVoteNum());
    userRepository.save(user);
    RsEventDto rsEvent = rsEventDto.get();
    rsEvent.setVoteNum(rsEvent.getVoteNum() + vote.getVoteNum());
    rsEventRepository.save(rsEvent);
  }

  public void buy(Trade trade, int id) {
    Optional<RsEventDto> rsEventDto = rsEventRepository.findById(id);
    Optional<TradeDto> tradeDto = tradeRepository.findById(trade.getRank());
    if (!rsEventDto.isPresent()) {
      throw new RuntimeException();
    }
    if (!tradeDto.isPresent()) {
      TradeDto tradeDtoSave = TradeDto.builder()
              .amount(trade.getAmount())
              .rank(trade.getRank())
              .rsEvent(rsEventDto.get())
              .build();
      tradeRepository.save(tradeDtoSave);
    }else {
      if (trade.getAmount()>tradeDto.get().getAmount()){
        TradeDto tradeDtoSave = TradeDto.builder()
                .amount(trade.getAmount())
                .rank(trade.getRank())
                .rsEvent(rsEventDto.get())
                .build();
        tradeRepository.save(tradeDtoSave);
      }else {
        throw new IsBadRequestException();
      }
    }
  }
  public List<RsEvent> getRsEvents(Integer start, Integer end){
    List<RsEventDto> rsEventDtos = rsEventRepository.findAll();
    List<RsEvent> rsEvents =
            rsEventDtos.stream()
                    .map(
                            item ->
                                    RsEvent.builder()
                                            .eventName(item.getEventName())
                                            .keyword(item.getKeyword())
                                            .userId(item.getId())
                                            .voteNum(item.getVoteNum())
                                            .rank(item.getRank())
                                            .build())
                    .collect(Collectors.toList());

//    rsEvents.sort(Comparator.comparingInt(RsEvent::getVoteNum));
    Collections.sort(rsEvents);
    if (start == null || end == null) {
      return rsEvents;
    }
    return rsEvents.subList(start - 1, end);
  }

  @ExceptionHandler(IsBadRequestException.class)
  public static ResponseEntity handlerExceptions(Exception ex) {
    return ResponseEntity.badRequest().build();
  }

}
