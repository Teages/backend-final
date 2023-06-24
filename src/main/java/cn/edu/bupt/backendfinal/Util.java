package cn.edu.bupt.backendfinal;

public class Util {
  public static String decodeAuth(String auth) {
    return auth != null && auth.startsWith("Bearer ") ? auth.substring(7) : null;
  }
  private Util() {}
}
