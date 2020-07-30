/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */

import 'dart:convert';

class UserInfo {
  int id;
  String realName = '';
  String login = '';
  String password = '';
  DateTime createDate;
  DateTime lastLogin;
  List<String> roles = [];

  UserInfo();

  bool isAdmin() {
    return ((roles != null) && roles.contains("ROLE_ADMIN"));
  }

  factory UserInfo.fromMap(final Map<String, dynamic> fields) {
    UserInfo userInfo = UserInfo();
    userInfo.id = fields['id'];
    userInfo.realName = fields['realName'];
    userInfo.login = fields['login'];
    if (fields['createDate'] != null) {
      userInfo.createDate = DateTime.parse(fields['createDate'].toString());
    }
    if (fields['lastLogin'] != null) {
      userInfo.lastLogin = DateTime.parse(fields['lastLogin'].toString());
    }
    userInfo.roles = List.from(fields['roles']);
    userInfo.password = '';
    return userInfo;
  }

  factory UserInfo.fromJsonString(final String jsonString) {
    Map<String, dynamic> fields = jsonDecode(jsonString);
    return UserInfo.fromMap(fields);
  }

  static List<UserInfo> listFromJsonString(final String jsonString) {
    List<UserInfo> userInfoList = List<UserInfo>();
    dynamic users = jsonDecode(jsonString);
    users.forEach((element) {
      userInfoList.add(UserInfo.fromMap(element));
    });
    return userInfoList;
  }

  Map toJson() {
    return {
      'id' : id,
      'realName' : realName,
      'login' : login,
      'password': password,
      'roles' : roles.toList()
    };
  }
}