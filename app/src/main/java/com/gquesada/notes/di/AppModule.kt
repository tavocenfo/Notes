package com.gquesada.notes.di

import androidx.room.Room
import com.gquesada.notes.core.network.AuthInterceptor
import com.gquesada.notes.data.database.database.AppDatabase
import com.gquesada.notes.data.datasources.DatabaseNoteDataSource
import com.gquesada.notes.data.datasources.DatabaseTagDataSource
import com.gquesada.notes.data.datasources.DatabaseUserDataSource
import com.gquesada.notes.data.datasources.RemoteNoteDataSource
import com.gquesada.notes.data.datasources.RemoteTagDataSource
import com.gquesada.notes.data.datasources.RemoteUserDataSource
import com.gquesada.notes.data.datasources.SharedPreferencesDataSource
import com.gquesada.notes.data.network.NoteApiService
import com.gquesada.notes.data.network.TagApiService
import com.gquesada.notes.data.network.UserApiService
import com.gquesada.notes.data.repositories.NoteRepositoryImpl
import com.gquesada.notes.data.repositories.TagRepositoryImpl
import com.gquesada.notes.data.repositories.UserRepositoryImpl
import com.gquesada.notes.domain.repositories.NoteRepository
import com.gquesada.notes.domain.repositories.TagRepository
import com.gquesada.notes.domain.repositories.UserRepository
import com.gquesada.notes.domain.usecases.AddNoteUseCase
import com.gquesada.notes.domain.usecases.AddTagUseCase
import com.gquesada.notes.domain.usecases.DeleteNoteUseCase
import com.gquesada.notes.domain.usecases.DeleteTagUseCase
import com.gquesada.notes.domain.usecases.EditNoteUseCase
import com.gquesada.notes.domain.usecases.EditTagUseCase
import com.gquesada.notes.domain.usecases.GetCurrentUserUseCase
import com.gquesada.notes.domain.usecases.GetNotesUseCase
import com.gquesada.notes.domain.usecases.GetTagListUseCase
import com.gquesada.notes.domain.usecases.LoginUseCase
import com.gquesada.notes.domain.usecases.LogoutUseCase
import com.gquesada.notes.ui.addnotes.viewmodels.AddNoteViewModel
import com.gquesada.notes.ui.login.viewmodels.LoginViewModel
import com.gquesada.notes.ui.main.viewmodels.MainViewModel
import com.gquesada.notes.ui.notes.viewmodels.NoteListViewModel
import com.gquesada.notes.ui.tag.viewmodels.TagListViewModel
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

const val BASE_URL = "http://192.168.1.14:3000"
val module = module {

    // Dependencias Network (Retrofit)
    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    single<Retrofit>(named("AuthRetrofit")) {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient.Builder().addInterceptor(AuthInterceptor(get(), get())).build())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    single { get<Retrofit>(named("AuthRetrofit")).create(TagApiService::class.java) }

    single { get<Retrofit>(named("AuthRetrofit")).create(NoteApiService::class.java) }

    single { get<Retrofit>().create(UserApiService::class.java) }

    // Dependencias Database (Room)
    single<AppDatabase> {
        Room.databaseBuilder(
            androidApplication().applicationContext,
            AppDatabase::class.java,
            "notes-db"
        ).build()
    }

    single { get<AppDatabase>().getTagDao() }
    single { get<AppDatabase>().getNotesDao() }
    single { get<AppDatabase>().getUserDao() }

    // Dependencias Datasources
    single { DatabaseNoteDataSource(get(), get()) }
    single { DatabaseTagDataSource(get()) }
    single { DatabaseUserDataSource(get()) }
    single { RemoteTagDataSource(get()) }
    single { RemoteNoteDataSource(get()) }
    single { RemoteUserDataSource(get()) }
    single { SharedPreferencesDataSource(androidContext()) }

    // Dependencias Repositorios
    single<NoteRepository> { NoteRepositoryImpl(get(), get(), get()) }
    single<TagRepository> { TagRepositoryImpl(get(), get()) }
    single<UserRepository> { UserRepositoryImpl(get(), get()) }

    // Dependencias UseCases
    factory { AddNoteUseCase(get()) }
    factory { AddTagUseCase(get()) }
    factory { DeleteNoteUseCase(get()) }
    factory { DeleteTagUseCase(get()) }
    factory { EditNoteUseCase(get()) }
    factory { EditTagUseCase(get()) }
    factory { GetNotesUseCase(get()) }
    factory { GetTagListUseCase(get()) }
    factory { LoginUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }
    factory { LogoutUseCase(get()) }

    // Dependencias ViewModels
    viewModel { NoteListViewModel(get(), get()) }
    viewModel { AddNoteViewModel(get(), get()) }
    viewModel { MainViewModel(get(), get()) }
    viewModel { TagListViewModel(get(), get(), get(), get()) }
    viewModel { LoginViewModel(get()) }
}