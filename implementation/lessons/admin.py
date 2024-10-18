from django.contrib import admin
from .models import Instructor, Lesson, Client, Booking

admin.site.register(Instructor)
admin.site.register(Lesson)
admin.site.register(Client)
admin.site.register(Booking)
