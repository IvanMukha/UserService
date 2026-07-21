package com.IvanMukha.UserService.specification;

import com.IvanMukha.UserService.model.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<User> hasName(String name){
        return (root, query, criteriaBuilder) ->{
            if(name==null||name.isBlank()){
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),"%"+name.strip().toLowerCase()+"%");
        };
    }
    public static Specification<User> hasSurname(String surname){
        return (root, query, criteriaBuilder) ->{
            if(surname==null||surname.isBlank()){
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("surname")),"%"+surname.strip().toLowerCase()+"%");
        };
    }
}
