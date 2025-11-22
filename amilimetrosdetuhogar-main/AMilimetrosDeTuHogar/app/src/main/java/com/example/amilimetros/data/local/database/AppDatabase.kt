package com.example.amilimetros.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.amilimetros.data.local.user.*
import com.example.amilimetros.data.local.product.*
import com.example.amilimetros.data.local.animal.*
import com.example.amilimetros.data.local.cart.*
import com.example.amilimetros.data.local.adoption.*
import com.example.amilimetros.data.local.order.*
import com.example.amilimetros.data.local.logo.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        ProductEntity::class,
        AnimalEntity::class,
        CartItemEntity::class,
        AdoptionFormEntity::class,
        LogoEntity::class,
        OrderEntity::class,          // ✅ NUEVO
        OrderItemEntity::class       // ✅ NUEVO
    ],
    version = 4,  // ✅ INCREMENTAR DE 3 A 4
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun animalDao(): AnimalDao
    abstract fun cartDao(): CartDao
    abstract fun adoptionFormDao(): AdoptionFormDao
    abstract fun logoDao(): LogoDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "tienda_app_v5.db"

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val userCount = instance.userDao().count()
                        if (userCount == 0) {
                            seedDatabase(instance)
                        }
                    } catch (e: Exception) {

                    }
                }

                instance
            }
        }

        private suspend fun seedDatabase(db: AppDatabase) {

            db.userDao().insert(
                UserEntity(
                    name = "Admin",
                    email = "admin@amilimetros.cl",
                    phone = "+56911111111",
                    password = "Admin123!_",
                    isAdmin = true
                )
            )

            db.userDao().insert(
                UserEntity(
                    name = "Cliente Demo",
                    email = "cliente@amilimetros.cl",
                    phone = "+56922222222",
                    password = "Cliente123!_",
                    isAdmin = false
                )
            )

            // ========== PRODUCTOS ==========
            val products = listOf(
                ProductEntity(
                    name = "Alimento Premium Perro",
                    description = "Alimento balanceado para perros adultos 15kg",
                    price = 35990.0,
                    category = "Alimento",
                    stock = 50
                ),
                ProductEntity(
                    name = "Pelota Interactiva",
                    description = "Pelota de goma resistente con sonido",
                    price = 8990.0,
                    category = "Juguetes",
                    stock = 100
                ),
                ProductEntity(
                    name = "Collar Ajustable",
                    description = "Collar ajustable de nylon para perros",
                    price = 6990.0,
                    category = "Accesorios",
                    stock = 75
                ),
                ProductEntity(
                    name = "Arena para Gatos",
                    description = "Arena sanitaria aglomerante 10kg",
                    price = 12990.0,
                    category = "Alimento",
                    stock = 40
                ),
                ProductEntity(
                    name = "Rascador para Gatos",
                    description = "Rascador de sisal con plataforma",
                    price = 25990.0,
                    category = "Accesorios",
                    stock = 20
                )
            )
            products.forEach { db.productDao().insert(it) }

           
            val animals = listOf(
                AnimalEntity(
                    name = "Max",
                    species = "Perro",
                    breed = "Labrador",
                    age = 3,
                    description = "Perro cariñoso y juguetón, ideal para familias",
                    isAdopted = false
                ),
                AnimalEntity(
                    name = "Luna",
                    species = "Gato",
                    breed = "Siamés",
                    age = 2,
                    description = "Gata tranquila y afectuosa",
                    isAdopted = false
                ),
                AnimalEntity(
                    name = "Rocky",
                    species = "Perro",
                    breed = "Pastor Alemán",
                    age = 5,
                    description = "Perro guardián, entrenado y leal",
                    isAdopted = false
                ),
                AnimalEntity(
                    name = "Mimi",
                    species = "Gato",
                    breed = "Persa",
                    age = 1,
                    description = "Gatita juguetona y curiosa",
                    isAdopted = false
                )
            )
            animals.forEach { db.animalDao().insert(it) }
        }
    }
}