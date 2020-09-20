package com.thoughtworks.rslist.repository;

import com.thoughtworks.rslist.domain.Trade;
import com.thoughtworks.rslist.dto.TradeDto;
import com.thoughtworks.rslist.dto.UserDto;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TradeRepository extends CrudRepository<TradeDto, Integer> {
    List<TradeDto> findAll();
}
