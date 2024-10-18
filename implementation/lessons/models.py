from django.db import models
from django.contrib.auth.models import User

class Instructor(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE, null=True)  # Allow null temporarily
    name = models.CharField(max_length=100)
    phone = models.CharField(max_length=15)
    specialization = models.CharField(max_length=100)
    cities = models.CharField(max_length=200)

    def __str__(self):
        return self.name

class Lesson(models.Model):
    LESSON_TYPES = (
        ('Private', 'Private'),
        ('Group', 'Group'),
    )
    lesson_type = models.CharField(max_length=50, choices=LESSON_TYPES)
    location = models.CharField(max_length=100)
    city = models.CharField(max_length=100)
    instructor = models.ForeignKey(Instructor, on_delete=models.SET_NULL, null=True)
    schedule = models.CharField(max_length=100)
    availability = models.BooleanField(default=True)

class Client(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE, null=True)  # Allow null temporarily
    age = models.IntegerField()
    guardian = models.CharField(max_length=100, blank=True, null=True)


    def __str__(self):
        return self.user.username

class Booking(models.Model):
    client = models.ForeignKey(Client, on_delete=models.CASCADE)
    lesson = models.ForeignKey(Lesson, on_delete=models.CASCADE)
    is_active = models.BooleanField(default=True)
