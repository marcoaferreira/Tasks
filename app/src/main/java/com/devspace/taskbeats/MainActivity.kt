package com.devspace.taskbeats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            TasksDatabase::class.java, "database-tasks"
        ).build()
    }

    private val categoryDao: CategoryDao by lazy {
        db.getCategoryDao()
    }

    private val taskDao: TaskDao by lazy {
        db.getTaskDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        insertDefaultCategory()
//        insertDefaultTask()

        val rvCategory = findViewById<RecyclerView>(R.id.rv_categories)
        val rvTask = findViewById<RecyclerView>(R.id.rv_tasks)

        val taskAdapter = TaskListAdapter()
        val categoryAdapter = CategoryListAdapter()

        categoryAdapter.setOnClickListener { selected ->
//            val categoryTemp = categories.map { item ->
//                when {
//                    item.name == selected.name && !item.isSelected -> item.copy(isSelected = true)
//                    item.name == selected.name && item.isSelected -> item.copy(isSelected = false)
//                    else -> item
//                }
//            }
//
//            val taskTemp =
//                if (selected.name != "ALL") {
//                    tasks.filter { it.category == selected.name }
//                } else {
//                    tasks
//                }
//            taskAdapter.submitList(taskTemp)
//
//            categoryAdapter.submitList(categoryTemp)
        }

        rvCategory.adapter = categoryAdapter
        getCategoriesFromDatabase(categoryAdapter)

        rvTask.adapter = taskAdapter
        getTasksFromDatabase(taskAdapter)
    }

//    private fun insertDefaultCategory(){
//        val categoriesEntity = categories.map{
//            CategoryEntity(
//                name = it.name,
//                isSelected = it.isSelected
//            )
//        }
//
//        GlobalScope.launch(Dispatchers.IO) {
//        categoryDao.insertAll(categoriesEntity)
//        }
//    }

//    private fun insertDefaultTask(){
//        val taskEntities = tasks.map {
//            TaskEntity(
//                name = it.name,
//                category = it.category
//            )
//        }
//
//        GlobalScope.launch(Dispatchers.IO){
//            taskDao.insertAll(taskEntities)
//        }
//    }

    private fun getCategoriesFromDatabase(adapter: CategoryListAdapter){
        GlobalScope.launch(Dispatchers.IO) {
            val categoriesFromDb: List<CategoryEntity> = categoryDao.getAll()
            val categoriesUiData= categoriesFromDb.map{
                CategoryUiData(
                    name = it.name,
                    isSelected = it.isSelected
                )
            }
            adapter.submitList(categoriesUiData)
        }
    }

    private fun getTasksFromDatabase(adapter: TaskListAdapter){
        GlobalScope.launch(Dispatchers.IO) {
            val TasksFromDb: List<TaskEntity> = taskDao.getAll()
            val tasksUiData = TasksFromDb.map{
                TaskUiData(
                    category = it.category,
                    name = it.name
                )
            }
            adapter.submitList(tasksUiData)
        }
    }
}

//val categories = listOf(
//    CategoryUiData(
//        name = "ALL",
//        isSelected = false
//    ),
//    CategoryUiData(
//        name = "STUDY",
//        isSelected = false
//    ),
//    CategoryUiData(
//        name = "WORK",
//        isSelected = false
//    ),
//    CategoryUiData(
//        name = "WELLNESS",
//        isSelected = false
//    ),
//    CategoryUiData(
//        name = "HOME",
//        isSelected = false
//    ),
//    CategoryUiData(
//        name = "HEALTH",
//        isSelected = false
//    ),
//)
//
//val tasks = listOf(
//    TaskUiData(
//        "Ler 10 páginas do livro atual",
//        "STUDY"
//    ),
//    TaskUiData(
//        "45 min de treino na academia",
//        "HEALTH"
//    ),
//    TaskUiData(
//        "Correr 5km",
//        "HEALTH"
//    ),
//    TaskUiData(
//        "Meditar por 10 min",
//        "WELLNESS"
//    ),
//    TaskUiData(
//        "Silêncio total por 5 min",
//        "WELLNESS"
//    ),
//    TaskUiData(
//        "Descer o livo",
//        "HOME"
//    ),
//    TaskUiData(
//        "Tirar caixas da garagem",
//        "HOME"
//    ),
//    TaskUiData(
//        "Lavar o carro",
//        "HOME"
//    ),
//    TaskUiData(
//        "Gravar aulas DevSpace",
//        "WORK"
//    ),
//    TaskUiData(
//        "Criar planejamento de vídeos da semana",
//        "WORK"
//    ),
//    TaskUiData(
//        "Soltar reels da semana",
//        "WORK"
//    ),
//)