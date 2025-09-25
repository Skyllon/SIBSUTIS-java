package UserInfo;

abstract class Contact {
  String name;
  String number;

  abstract String getContactType();
}

class PersonalContact extends Contact implements ContactAvailability.Callable  {
  String birthDate;
  String extraInfo;
  String address;

  String getContactType() { return "Личный"; }
  String getFullInfo()    { return String.format(
    "Имя: %s\nНомер: %s\nДень рождения: %s\nОписание: %s\nАдрес: %s",
    name, number, birthDate, extraInfo, address);
  }

  @Override
  public void call() {

  }
}

class WorkContact extends Contact implements ContactAvailability.Callable,
ContactAvailability.Emailable {
  String company;
  String post;
  String email;

  String getContactType() { return "Рабочий"; }
  String getFullInfo()    { return String.format(
    "Имя: %s\nНомер: %s\nМесто работы: %s\nДолжность: %s\nПочта: %s",
    name, number, company, post, email);
  }

  @Override
  public void call() {

  }

  @Override
  public void sendEmail(String msg) {

  }
}
