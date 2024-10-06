package com.cafe.serviceImpl;

import com.cafe.JWT.CustomerUserDetailsService;
import com.cafe.JWT.JWTUtil;
import com.cafe.JWT.JwtFilter;
import com.cafe.constents.CafeConstants;
import com.cafe.dao.UserDao;
import com.cafe.model.User;
import com.cafe.service.UserService;
import com.cafe.utils.CafeUtils;
import com.cafe.utils.EmailUtil;
import com.cafe.wrapper.UserWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

    @Autowired(required = true)
    AuthenticationManager authenticationManager;

    @Autowired
    CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    JWTUtil jwtUtil;

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    EmailUtil emailUtil;

    @Override
    public ResponseEntity<String> signup(Map<String, String> requestMap) {
        log.info("inside signup {}", requestMap);
        try {
            if(validateSinUpMap(requestMap)){
                User user = userDao.findByEmailId(requestMap.get("email"));

                if(Objects.isNull(user)){
                    userDao.save(getUserFromMap(requestMap));
                    return CafeUtils.getResponseEntity("User Successfully Registered!", HttpStatus.OK);

                }else {
                   return CafeUtils.getResponseEntity("Email Already Exits", HttpStatus.BAD_REQUEST);
                }
            }
            else {
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private boolean validateSinUpMap(Map<String, String> reqestMap){

        if (reqestMap.containsKey("name") && reqestMap.containsKey("contactNumber") && reqestMap.containsKey("password")
                && reqestMap.containsKey("email")){
            return true;
        } else {
            return false;
        }
    }

    public User getUserFromMap(Map<String, String> requestMap){

        User user = new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false");
        user.setRole("User");

        return user;
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Inside login");

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"),requestMap.get("password"))
            );
            if(auth.isAuthenticated()){
                if (customerUserDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")){
                    return new ResponseEntity<String>("{\"token\":\""+
                            jwtUtil.generateToken(customerUserDetailsService.getUserDetail().getEmail(),
                                    customerUserDetailsService.getUserDetail().getRole())+"\"}",
                            HttpStatus.OK);
                }
                else {
                    return new ResponseEntity<String>("waiting for admin approval", HttpStatus.BAD_REQUEST);
                }

            }
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<String>("Bad credentials.", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        log.info("Inside get");
        try {
            if (jwtFilter.isAdmin()){

                return new ResponseEntity<>(userDao.getAllUser(),HttpStatus.OK);
            }else {
                return new ResponseEntity<>(new ArrayList<>(),HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()){
                Optional<User> optional = userDao.findById(Integer.valueOf(requestMap.get("id")));

                if (!optional.isEmpty()){
                    userDao.updateStatus(requestMap.get("status"), Integer.valueOf(requestMap.get("id")));
                    sendMailToAllAdmin(requestMap.get("status"),optional.get().getEmail(),userDao.getAllAdmin());
                    return CafeUtils.getResponseEntity("User status updated successfully",HttpStatus.OK);

                }else {
                    return CafeUtils.getResponseEntity("User id does not exit.", HttpStatus.OK);

                }
            }else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {

        allAdmin.remove(jwtFilter.getCurrentUser());
        if(status != null && status.equalsIgnoreCase("true")){
            emailUtil.sendSimpleMsg(jwtFilter.getCurrentUser(),"Account Disabled",
                    "USER:- "+user+"\n is disabled by \nADMIN:- "+jwtFilter.getCurrentUser(), allAdmin);
        }
    }
}
