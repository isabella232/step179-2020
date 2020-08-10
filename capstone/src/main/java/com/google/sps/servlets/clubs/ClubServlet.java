package com.google.sps.servlets;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* Servlet that stores and returns data relating to clubs. */
@WebServlet("/clubs")
public class ClubServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    UserService userService = UserServiceFactory.getUserService();
    String userEmail = userService.getCurrentUser().getEmail();

    Entity clubEntity = retrieveClub(request, datastore).asSingleEntity();
    if (clubEntity != null) {
      String name = clubEntity.getProperty(Constants.PROPERTY_NAME).toString();
      ImmutableList<String> members =
          ImmutableList.copyOf((ArrayList<String>) clubEntity.getProperty(Constants.MEMBER_PROP));
      ImmutableList<String> officers =
          ImmutableList.copyOf((ArrayList<String>) clubEntity.getProperty(Constants.OFFICER_PROP));
      String description = clubEntity.getProperty(Constants.DESCRIP_PROP).toString();
      String website = clubEntity.getProperty(Constants.WEBSITE_PROP).toString();
      boolean isOfficer = officers.contains(userEmail);
      Club club = new Club(name, members, officers, description, website);
      Gson gson = new Gson();
      JsonElement jsonElement = gson.toJsonTree(club);
      jsonElement.getAsJsonObject().addProperty("isOfficer", isOfficer);
      String json = gson.toJson(jsonElement);
      response.setContentType("text/html;");
      response.getWriter().println(json);
    } else {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    BlobstoreService blobstore = BlobstoreServiceFactory.getBlobstoreService();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    doPostHelper(request, response, blobstore, datastore);
  }

  public void doPostHelper(
      HttpServletRequest request,
      HttpServletResponse response,
      BlobstoreService blobstore,
      DatastoreService datastore)
      throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    String founderEmail = userService.getCurrentUser().getEmail();

    // Check if club name is valid

    PreparedQuery prepared = retrieveClub(request, datastore);
    boolean isValid = Iterables.isEmpty(prepared.asIterable());

    if (isValid) {
      String clubName = request.getParameter(Constants.PROPERTY_NAME);
      String description = request.getParameter(Constants.DESCRIP_PROP);
      String website = request.getParameter(Constants.WEBSITE_PROP);
      BlobKey key = BlobstoreUtil.getBlobKey(request, Constants.LOGO_PROP, blobstore);

      Entity clubEntity = new Entity(Constants.CLUB_ENTITY_PROP, clubName);
      clubEntity.setProperty(Constants.CLUB_NAME_PROP, clubName);
      clubEntity.setProperty(Constants.DESCRIP_PROP, description);
      clubEntity.setProperty(Constants.WEBSITE_PROP, website);
      clubEntity.setProperty(Constants.MEMBER_PROP, ImmutableList.of(founderEmail));
      clubEntity.setProperty(Constants.OFFICER_PROP, ImmutableList.of(founderEmail));
      clubEntity.setProperty(Constants.LOGO_PROP, key);
      datastore.put(clubEntity);
    }

    response.sendRedirect("/registration-msg.html?is-valid=" + isValid);
  }

  private PreparedQuery retrieveClub(HttpServletRequest request, DatastoreService datastore) {
    Query query =
        new Query("Club")
            .setFilter(
                new FilterPredicate(
                    Constants.PROPERTY_NAME,
                    FilterOperator.EQUAL,
                    request.getParameter(Constants.PROPERTY_NAME)));
    PreparedQuery prepared = datastore.prepare(query);
    return prepared;
  }
}
