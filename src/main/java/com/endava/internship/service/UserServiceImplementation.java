package com.endava.internship.service;

import com.endava.internship.domain.Privilege;
import com.endava.internship.domain.User;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UserServiceImplementation implements UserService {

    @Override
    public List<String> getFirstNamesReverseSorted (List<User> users) {
        return users.stream()
                .map(User::getFirstName)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    @Override
    public List<User> sortByAgeDescAndNameAsc (final List<User> users) {
        return users.stream()
                .sorted(Comparator.comparing(User::getAge, Comparator.reverseOrder())
                        .thenComparing(User::getFirstName))
                        .collect(Collectors.toList());
    }

    @Override
    public List<Privilege> getAllDistinctPrivileges (final List<User> users) {
        return users.stream()
                .flatMap(user -> user.getPrivileges().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> getUpdateUserWithAgeHigherThan (final List<User> users, final int age) {
        return users.stream()
                .filter(user -> user.getAge() > age && user.getPrivileges().contains(Privilege.UPDATE))
                .findAny();
    }

    @Override
    public Map<Integer, List<User>> groupByCountOfPrivileges (final List<User> users) {
        return users.stream().collect(Collectors.groupingBy(user -> user.getPrivileges().size()));
    }


    @Override
    public double getAverageAgeForUsers (final List<User> users) {
        return users.stream()
                .mapToInt(User::getAge)
                .average()
                .orElse(-1);
    }

    @Override
    public Optional<String> getMostFrequentLastName (final List<User> users) {
        return users.stream()
                .map(User::getLastName)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .collect(Collectors.groupingBy(Map.Entry::getValue))
                .entrySet().stream()
                .max(Map.Entry.comparingByKey())
                .filter(entry -> entry.getValue().size() == 1)
                .map(entry -> entry.getValue().get(0).getKey());
    }

    @Override
    public final List<User> filterBy (final List<User> users, final Predicate<User>... predicates) {
        return users.stream()
                .filter(Stream.of(predicates)
                        .reduce(Predicate::and).orElse(x -> true))
                        .collect(Collectors.toList());
    }

    @Override
    public String convertTo (final List<User> users, final String delimiter, final Function<User, String> mapFun) {
        return users.stream().map(mapFun).collect(Collectors.joining(delimiter));
    }

    @Override
    public Map<Privilege, List<User>> groupByPrivileges (List<User> users) {
        return Stream.of(Privilege.values())
                .map(privilege -> new AbstractMap.SimpleEntry<>(privilege, users.stream()
                        .filter(user -> user.getPrivileges().contains(privilege))
                        .collect(Collectors.toList())))
                        .filter(entry -> entry.getValue().size() > 0)
                        .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }

    @Override
    public Map<String, Long> getNumberOfLastNames (final List<User> users) {
        return users.stream().collect(Collectors.groupingBy(User::getLastName, Collectors.counting()));
    }
}