package UserInfo;

abstract class Contact {
  String name;
  String number;

  abstract String getContactType();
  abstract String getFullInfo();
}
