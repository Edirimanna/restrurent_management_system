package com.cafe.serviceImpl;

import com.cafe.constents.CafeConstants;
import com.cafe.dao.UserDao;
import com.cafe.model.User;
import com.cafe.service.UserService;
import com.cafe.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

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
        user.setStatus(requestMap.get("status"));
        user.setRole(requestMap.get("false"));

        return user;
    }
}
