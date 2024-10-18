from django.urls import path
from . import views

urlpatterns = [
    path('', views.home, name='home'),  # Set home as the main entry
    path('lessons/', views.index, name='index'),
    path('register_instructor/', views.register_instructor, name='register_instructor'),
    path('register_client/', views.register_client, name='register_client'),
    path('login/', views.login_view, name='login'),
    path('book_lesson/<int:lesson_id>/', views.book_lesson, name='book_lesson'),
]
