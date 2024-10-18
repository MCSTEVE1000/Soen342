from django import forms
from django.contrib.auth.models import User
from .models import Instructor, Client

class InstructorRegistrationForm(forms.ModelForm):
    username = forms.CharField(max_length=150)
    password = forms.CharField(widget=forms.PasswordInput)

    class Meta:
        model = Instructor
        fields = ['username', 'password', 'specialization', 'cities']

class ClientRegistrationForm(forms.ModelForm):
    username = forms.CharField(max_length=150)
    password = forms.CharField(widget=forms.PasswordInput)
    age = forms.IntegerField()

    class Meta:
        model = Client
        fields = ['username', 'password', 'age', 'guardian']

