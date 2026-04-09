package com.example.movieapp.di;

import android.app.Application;

import androidx.room.Room;

import com.example.movieapp.data.database.MovieDao;
import com.example.movieapp.data.database.MovieDatabase;
import com.example.movieapp.data.networkServices.MovieApiService;
import com.example.movieapp.data.repo.MovieDBRepositoryImpl;
import com.example.movieapp.data.repo.MovieDetailsRepositoryImpl;
import com.example.movieapp.data.repo.MovieRepositoryImpl;
import com.example.movieapp.domain.repoInterfaces.MovieDBRepository;
import com.example.movieapp.domain.repoInterfaces.MovieDetailsRepository;
import com.example.movieapp.domain.repoInterfaces.MovieRepository;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class AppModule {

    @Provides
    public static MovieDatabase provideMovieDatabase(Application application) {
        return Room.databaseBuilder(application, MovieDatabase.class, "movie_db")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Provides
    public static MovieDao provideMovieDao(MovieDatabase database) {
        return database.movieDao();
    }

    @Provides
    public static MovieRepository provideMovieRepository(MovieApiService apiService) {
        return new MovieRepositoryImpl(apiService);
    }

    @Provides
    public static MovieDetailsRepository provideMovieDetailsRepository(MovieApiService apiService) {
        return new MovieDetailsRepositoryImpl(apiService);
    }

    @Provides
    public static MovieDBRepository provideMovieDBRepository(MovieDao movieDao, MovieRepository movieRepository) {
        return new MovieDBRepositoryImpl(movieDao, movieRepository);
    }
}
