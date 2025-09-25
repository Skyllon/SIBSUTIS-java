package UserInfo;


public abstract class UserInfo {
  private final int MAX_NUMBER_LENGTH = 15;
  protected String userNumber;
  protected String userName;
  protected String userSurname;

  // Pre-init
  {
    userNumber  = "undefined";
    userName    = "undefined";
    userSurname = "undefined";
  } public UserInfo() {}

  public UserInfo(String userNumber, String userName, String userSurname) {
    this.userNumber  = userNumber;
    this.userName    = userName;
    this.userSurname = userSurname;
  }

  public void setNumber(String number) {
    if (number instanceof String && number.length() <= MAX_NUMBER_LENGTH)
      this.userNumber = number;
  }

  public void setName(String userName) {
    if (userName instanceof String)
      this.userName = userName;
  }

  public void setSurname(String userSurname) {
    if (userSurname instanceof String)
      this.userSurname = userSurname;
  }

  public String getNumber()  { return this.userNumber;  }
  public String getName()    { return this.userName;    }
  public String getSurname() { return this.userSurname; }

  public abstract String getContactType();
  public abstract String getFullInfo();
}
