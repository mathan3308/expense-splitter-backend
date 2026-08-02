package com.mathan.expensesplitter.service;

import com.mathan.expensesplitter.dto.auth.LoginRequest;
import com.mathan.expensesplitter.dto.auth.LoginResponse;
import com.mathan.expensesplitter.dto.auth.RegisterRequest;
import com.mathan.expensesplitter.dto.auth.RegisterResponse;

public interface UserService {

    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
}