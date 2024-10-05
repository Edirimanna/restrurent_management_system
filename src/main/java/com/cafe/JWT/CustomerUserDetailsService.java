package com.cafe.JWT;

import com.cafe.dao.UserDao;
import com.cafe.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

@Slf4j
@Service
public class CustomerUserDetailsService implements UserDetailsService {

    @Autowired
    UserDao userDao;

    private com.cafe.model.User userDetail;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("loadUserByUsername , {} ", username);

        userDetail =  userDao.findByEmailId(username);
        if (!Objects.isNull(userDetail)){
            return new org.springframework.security.core.userdetails.User(userDetail.getEmail(),
                    userDetail.getPassword(), new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("user not found.");
        }
    }
}
