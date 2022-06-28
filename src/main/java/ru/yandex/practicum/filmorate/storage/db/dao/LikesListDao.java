package ru.yandex.practicum.filmorate.storage.db.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class LikesListDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public LikesListDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveLikesListByFilm(Film film) {

        String sql = "INSERT INTO LIKESLIST (film_id, user_id) VALUES (?, ?)";

        Set<Integer> likes = film.getLikes();

        for (Integer like : likes) {
            jdbcTemplate.update(sql, film.getId(), like);
        }

    }

    public Set<Integer> getLikesListById(Integer id) {

        String sql = "SELECT user_id FROM LIKESLIST WHERE film_id = ?";

        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("user_id"), id));

    }

    public void delete(Integer id) {

        String sql = "DELETE FROM LIKESLIST WHERE film_id = ?";

        jdbcTemplate.update(sql, id);

    }

    public List<Integer> getTopFilms(int count) {
        String sql = "SELECT film_id FROM LIKESLIST GROUP BY film_id ORDER BY COUNT(user_id) DESC LIMIT ?";
        return jdbcTemplate.queryForList(sql, Integer.class, count);
    }

    // Получение id топа фильмов за N год. Сортировка по лайкам
    public List<Integer> getTopFilmsByYear(int year) {
        String sql = "SELECT fy.film_id " +
                "FROM (SELECT film_id " +
                "FROM FILMS " +
                "WHERE year(releasedate) = ?) AS fy " +
                "INNER JOIN likeslist l on fy.film_id = l.film_id " +
                "GROUP BY l.film_id " +
                "ORDER BY COUNT(user_id) DESC";
        return jdbcTemplate.queryForList(sql, Integer.class, year);
    }

    // Получение id топа фильмов за N год. Сортировка по лайкам
    public List<Integer> getTopFilmsByGenre(int genre) {
        String sql = "SELECT fg.film_id " +
                "FROM (SELECT films.film_id " +
                "FROM films " +
                "INNER JOIN genreslist as g ON films.film_id = g.film_id" +
                "WHERE genre_id = ?) AS fg " +
                "INNER JOIN likeslist l on fg.film_id = l.film_id " +
                "GROUP BY l.film_id " +
                "ORDER BY COUNT(user_id) DESC";
        return jdbcTemplate.queryForList(sql, Integer.class, genre);
    }
}
