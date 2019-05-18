package edu.uci.ics.jkotha.service.movies.support;

import edu.uci.ics.jkotha.service.movies.Models.*;
import edu.uci.ics.jkotha.service.movies.MovieService;
import edu.uci.ics.jkotha.service.movies.logger.ServiceLogger;
import org.glassfish.jersey.internal.util.ExceptionUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class FunctionsRequired {
    public static String getMessage(int code){
        switch (code){
            case -17:
                return " SessionID not provided in request header.";
            case -16:
                return " Email not provided in request header.";
            case -14:
                return "Privilege level out of valid range.";
            case -11:
                return "Email has invalid format";
            case -10:
                return "Email has invalid length";
            case -3:
                return "JSON parse exception";
            case -2:
                return "JSON mapping exception";
            case 141:
                return "User has insufficient privilege.";
            case 210:
                return "Found movies with search parameters";
            case 211:
                return "No movies found with search parameters";
            case 212:
                return "Found stars with search parameters";
            case 213:
                return "No stars found with search parameters";
            case 214:
                return "Movie successfully added.";
            case 215:
                return "Could not add movie";
            case 216:
                return "Movie already exists.";
            case 217:
                return "Genre successfully added.";
            case 218:
                return "Genre could not be added";
            case 219:
                return "Genres successfully retrieved.";
            case 220:
                return "Star successfully added.";
            case 221:
                return "Could not add star";
            case 222:
                return "Star already exists.";
            case 230:
                return "Star successfully added to movie";
            case 231:
                return "Could not add star to movie";
            case 232:
                return "Star already exists in movie";
            case 240:
                return "Movie successfully removed.";
            case 241:
                return "Could not remove movie.";
            case 242:
                return "Movie has been already removed.";
            case 250:
                return "Rating successfully updated";
            case 251:
                return "Could not update rating.";
        }
        return null;
    }

//    public static boolean isValidEmail(String email){
//        String[] components1 = email.split("@",0);
//        if (components1.length!=2 | components1[0].length()==0)
//            return false;
//        String[] components2 = components1[1].split("\\.",0);
//        if (components2.length<2 ){
//            return false;
//        }
//        else {
//            if( components2[0].length()==0 | components2[1].length()==0)
//                return false;
//        }
//        return true;
//    }

//    private StarModel[] toStarArray(ResultSet resultSet){
//        ArrayList<StarModel> finalResult = new ArrayList<>();
//
//        try{
//            while (resultSet.next()){
//
//                StarModel result1 = new StarModel(resultSet.getString("movieId"),
//                        resultSet.getString("name"),resultSet.getInt("year"));
//                finalResult.add(result1);
//                //ServiceLogger.LOGGER.info(result1.toString());
//            }
//        }catch (SQLException e){
//            ServiceLogger.LOGGER.info("SQL exception");
//        }
//        StarModel[] result = new edu.uci.ics.jkotha.service.movies.models.StarModel[finalResult.size()];
//        for(int i=0;i<result.length;i++){
//            //ServiceLogger.LOGGER.info("user:"+i);
//            result[i]=finalResult.get(i);
//            //ServiceLogger.LOGGER.info(result[i].toString());
//        }
//        return result;
//    }

    private static String[] stringSplitter(String string, int i ){
        if (string == null)
            return null;
        switch (i){
            case 1:
                return string.split(";");
            case 2:
                return string.split("=");
        }
        return null;
    }

    private static GenreModel getGenreModel(String input){
        String[] split = stringSplitter(input,2);
        if (split!=null){
            if (split.length==2)
                return new GenreModel(Integer.parseInt(split[0]),split[1]);
        }
        return null;
    }

    private static StarModelForMovies getStarModel(String input){

        String[] split = stringSplitter(input,2);
        if (split!=null){
            if (split.length==2)
                return new StarModelForMovies(split[0],split[1]);
        }
        return null;
    }

    public static GenreModel[] toGenreArray(String preSplitString){
        ArrayList<GenreModel> finalResult = new ArrayList<>();
        String[] split = stringSplitter(preSplitString,1);
        if (split==null)
            return null;
        for (String s: split){
            finalResult.add(getGenreModel(s));
        }
        GenreModel[] result = new GenreModel[finalResult.size()];
        for(int i=0;i<finalResult.size();i++){
            result[i]=finalResult.get(i);
        }
        return result;
    }


    public static StarModelForMovies[] getStarArray(String preSplitString){
        ArrayList<StarModelForMovies> finalResult = new ArrayList<>();
        String[] split = stringSplitter(preSplitString,1);
        if(split == null)
            return null;
        for (String s: split){
            //ServiceLogger.LOGGER.info(s);
            finalResult.add(getStarModel(s));
        }
        StarModelForMovies[] result = new StarModelForMovies[finalResult.size()];
        for(int i=0;i<finalResult.size();i++){
            result[i]=finalResult.get(i);
        }
        return result;
    }

//    public static MovieModel[] forPrivilegedSearch(ResultSet rs){
//        ArrayList<MovieModel> list = new ArrayList<>();
//        try {
//            if (!rs.next())
//                return null;
//            else {
//                rs.previous();
//            }
//            while (rs.next()){
//                MovieModel mm = new MovieModel(rs.getString("id"),rs.getString("title"),
//                        rs.getString("director"),rs.getInt("year"),rs.getString("backdrop_path"),
//                        rs.getInt("budget"),rs.getString("overview"),
//                        rs.getString("poster_path"),rs.getInt("revenue"),rs.getFloat("rating"),
//                        rs.getInt("numVotes"),toGenreArray(rs.getString("genre")),
//                        getStarArray(rs.getString("stars")),rs.getInt("hidden"));
//                list.add(mm);
//
//            }
//        }catch (SQLException e){
//            ServiceLogger.LOGGER.warning(ExceptionUtils.exceptionStackTraceAsString(e));
//        }
//        if (list.size()==0)
//            return null;
//        MovieModel[] result = new MovieModel[list.size()];
//        for (int i=0;i<list.size();++i){
//            result[i] = list.get(i);
//        }
//        return result;
//    }

//    public static MovieModel[] forUserSearch(ResultSet rs){
//        ArrayList<MovieModel> list = new ArrayList<>();
//        try {
//            if (!rs.next())
//                return null;
//            else {
//                rs.previous();
//            }
//            while (rs.next()){
//                MovieModel mm = new MovieModel(rs.getString("id"),rs.getString("title"),
//                        rs.getString("director"),rs.getInt("year"),rs.getString("backdrop_path"),
//                        rs.getInt("budget"),rs.getString("overview"),
//                        rs.getString("poster_path"),rs.getInt("revenue"),rs.getFloat("rating"),
//                        rs.getInt("numVotes"),toGenreArray(rs.getString("genre")),
//                        getStarArray(rs.getString("stars")),null);
//                list.add(mm);
//
//            }
//        }catch (SQLException e){
//            ServiceLogger.LOGGER.warning(ExceptionUtils.exceptionStackTraceAsString(e));
//        }
//        if (list.size()==0)
//            return null;
//        MovieModel[] result = new MovieModel[list.size()];
//        for (int i=0;i<list.size();++i){
//            result[i] = list.get(i);
//        }
//        return result;
//    }

//    public static MovieModel[] forUserSearch1(ResultSet rs){
//        ArrayList<MovieModel> list = new ArrayList<>();
//        try {
//            if (!rs.next())
//                return null;
//            else {
//                rs.previous();
//            }
//            while (rs.next()){
//                MovieModel mm = new MovieModel(rs.getString("id"),rs.getString("title"),
//                        rs.getString("director"),rs.getInt("year"),rs.getString("backdrop_path"),
//                        rs.getInt("budget"),rs.getString("overview"),
//                        rs.getString("poster_path"),rs.getInt("revenue"),rs.getFloat("rating"),
//                        rs.getInt("numVotes"),toGenreArray(rs.getString("genre")),
//                        getStarArray(rs.getString("stars")),null);
//                list.add(mm);
//
//            }
//        }catch (SQLException e){
//            ServiceLogger.LOGGER.warning(ExceptionUtils.exceptionStackTraceAsString(e));
//        }
//        if (list.size()==0)
//            return null;
//        MovieModel[] result = new MovieModel[list.size()];
//        for (int i=0;i<list.size();++i){
//            result[i] = list.get(i);
//        }
//        return result;
//    }

//
//    public static MovieModelForStars[] getMovieModelforStars(String presplitString){
//        if (presplitString==null)
//            return null;
//        String[] splitMovies = stringSplitter(presplitString,1);
//        ArrayList<MovieModelForStars> list = new ArrayList<>();
//        if (splitMovies==null)
//            return null;
//        for (String s:splitMovies){
//            String[] splitModel = stringSplitter(s,2);
//            MovieModelForStars movie;
//            if (splitModel!=null){
//                movie = new MovieModelForStars(splitModel[0],splitModel[1]);
//                list.add(movie);
//            }
//        }
//        if (list.size()==0)
//            return null;
//        MovieModelForStars[] result = new MovieModelForStars[list.size()];
//        for (int i=0;i<list.size();++i){
//            result[i] = list.get(i);
//        }
//        return result;
//    }

    public static StarModel[] getStarModel(ResultSet rs){
        ArrayList<StarModel> list  = new ArrayList<>();
        StarModel starModel;
        try {
            while (rs.next()){
                starModel = new StarModel(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getInt("birthYear")
                );
                list.add(starModel);
                //ServiceLogger.LOGGER.info(starModel.getMovieId());
            }
        }catch (SQLException e){
            ServiceLogger.LOGGER.warning(ExceptionUtils.exceptionStackTraceAsString(e));
        }
        if (list.size()==0)
            return null;
        StarModel[] finalResult = new StarModel[list.size()];
        for (int i=0;i<list.size();i++){
            finalResult[i] = list.get(i);
        }
        ServiceLogger.LOGGER.info("final result ready");
        return finalResult;
    }

    public static String getNewMovieId(String oldId){
        if (oldId==null){
            return "cs0000001";
        }
        int id = Integer.parseInt(oldId.substring(2))+1;
        return  "cs"+String.format("%07d",id);
    }

    public static String getNewStarId(String oldId){
        if (oldId==null){
            return "ss0000001";
        }
        int id = Integer.parseInt(oldId.substring(2))+1;
        return  "ss"+String.format("%07d",id);
    }

    public static MovieModel[] getMovieModelForSearch(ResultSet rs,boolean privilege){
        ArrayList<MovieModel> list = new ArrayList<>();
        try {
            if (!rs.next())
                return null;
            else {
                rs.previous();
            }
            while (rs.next()){
                MovieModel mm;
                if (privilege) {
                    mm = new MovieModel(rs.getString("id"),rs.getString("title"),
                            rs.getString("director"),rs.getInt("year"),rs.getString("backdrop_path"),
                            rs.getInt("budget"),rs.getString("overview"),
                            rs.getString("poster_path"),rs.getInt("revenue"),rs.getFloat("rating"),
                            rs.getInt("numVotes"),null,
                            null,rs.getInt("hidden"));
                }
                else {
                    mm = new MovieModel(rs.getString("id"),rs.getString("title"),
                            rs.getString("director"),rs.getInt("year"),rs.getString("backdrop_path"),
                            rs.getInt("budget"),rs.getString("overview"),
                            rs.getString("poster_path"),rs.getInt("revenue"),rs.getFloat("rating"),
                            rs.getInt("numVotes"),null,
                            null,null);
                }
                list.add(mm);
            }
        }catch (SQLException e){
            ServiceLogger.LOGGER.warning(ExceptionUtils.exceptionStackTraceAsString(e));
        }
        if (list.size()==0)
            return null;
        MovieModel[] result = new MovieModel[list.size()];
        for (int i=0;i<list.size();++i){
            result[i] = list.get(i);
        }
        return result;
    }

    public static int[] insertGenres(GenreModel[] genres,String movieId){
        int[] result = new int[genres.length];
        int i =0;
        for (GenreModel g:genres){
            try {
                String test = "select id from genres where name = ?";
                PreparedStatement testQuery = MovieService.getCon().prepareStatement(test);
                testQuery.setString(1,g.getName());
                ResultSet rs = testQuery.executeQuery();
                if (rs.next()){
                    String insert = "insert into genres_in_movies(genreId, movieId) values (?,?)";
                    PreparedStatement insertQuery = MovieService.getCon().prepareStatement(insert);
                    insertQuery.setInt(1,rs.getInt("id"));
                    insertQuery.setString(2,movieId);
                    insertQuery.execute();
                    result[i] = rs.getInt("id");
                    i++;
                    continue;
                }
                else {
                    String genreInsert = "insert into genres(name) values (?)";
                    PreparedStatement insertGenereQuery = MovieService.getCon().prepareStatement(genreInsert);
                    insertGenereQuery.setString(1,g.getName());
                    insertGenereQuery.execute();
                }
                rs= testQuery.executeQuery();
                rs.next();
                String insert = "insert into genres_in_movies(genreId, movieId) values (?,?)";
                PreparedStatement insertQuery = MovieService.getCon().prepareStatement(insert);
                insertQuery.setInt(1,rs.getInt("id"));
                insertQuery.setString(2,movieId);
                insertQuery.execute();
                result[i] = rs.getInt("id");
                i++;
            }
            catch (SQLException e){
                ServiceLogger.LOGGER.warning(ExceptionUtils.exceptionStackTraceAsString(e));
            }
        }
        return result;
    }

    public static GenreModel[] getGenres(){
        GenreModel[] genres = null;
        try{
            String s="select id, name from genres";
            PreparedStatement statement = MovieService.getCon().prepareStatement(s);
            ResultSet rs = statement.executeQuery();
            ArrayList<GenreModel> list = new ArrayList<>();
            while (rs.next()){
                GenreModel gm = new GenreModel(
                        rs.getInt("id"),
                        rs.getString("name")
                );
                list.add(gm);
            }
            genres = new GenreModel[list.size()];
            for (int i=0;i<list.size();++i){
                genres[i] = list.get(i);
            }
        }catch (SQLException e){
            ServiceLogger.LOGGER.warning(ExceptionUtils.exceptionStackTraceAsString(e));
        }
        return genres;
    }

    public static GenreModel[] getGenres(ResultSet rs){
        GenreModel[] genres;
        ArrayList<GenreModel> list = new ArrayList<>();
        try {
            while (rs.next()){
                GenreModel gm = new GenreModel(
                        rs.getInt("id"),
                        rs.getString("name")
                );
                list.add(gm);
            }
        }catch (SQLException e){
            ServiceLogger.LOGGER.warning(ExceptionUtils.exceptionStackTraceAsString(e));
        }

        genres = new GenreModel[list.size()];
        for (int i=0;i<list.size();++i){
            genres[i] = list.get(i);
        }
        return genres;
    }
}
