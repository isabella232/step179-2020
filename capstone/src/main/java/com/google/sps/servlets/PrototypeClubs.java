package com.google.sps.servlets;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/* Creates hardcoded club information for prototype. */
class PrototypeClubs {
  private PrototypeClubs() {}

  static final String CLUB_1 = "Club 1";
  static final String CLUB_2 = "Club 2";
  static final String CLUB_3 = "Club 3";

  static final ImmutableMap<String, Club> PROTOTYPE_CLUBS_MAP =
      ImmutableMap.of(
          CLUB_1,
              new Club(
                  CLUB_1,
                  ImmutableList.of("Megan", "Megha", "Kevin", "Chris", "Linh"),
                  ImmutableList.of("Megan", "Megha", "Kevin"),
                  "This is our club. It is very cool. We do lots of things. We help people who are in need of help and those who have fallen down and can&rsquo;t get up.",
                  "www.club1.com",
                  ""),
          CLUB_2,
              new Club(
                  CLUB_2,
                  ImmutableList.of("Megan", "Megha", "Kevin"),
                  ImmutableList.of("Megha"),
                  "We do cool club things",
                  "www.club2atSchool.com",
                  ""),
          CLUB_3,
              new Club(
                  CLUB_3,
                  ImmutableList.of("Kevin"),
                  ImmutableList.of("Kevin"),
                  "Lonely club :( please join",
                  "www.please-join.com",
                  ""));
}
