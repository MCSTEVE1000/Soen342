from django.shortcuts import render, redirect
from django.contrib import messages
from .models import Lesson, Instructor, Client, Booking
from django.contrib.auth.models import User
from django.contrib.auth import login, authenticate
from .forms import InstructorRegistrationForm, ClientRegistrationForm

def index(request):
    lessons = Lesson.objects.filter(availability=True)
    return render(request, 'index.html', {'lessons': lessons})

def register_instructor(request):
    if request.method == 'POST':
        form = InstructorRegistrationForm(request.POST)
        if form.is_valid():
            user = User.objects.create_user(
                username=form.cleaned_data['username'],
                password=form.cleaned_data['password']
            )
            Instructor.objects.create(
                user=user,
                specialization=form.cleaned_data['specialization'],
                cities=form.cleaned_data['cities']
            )
            messages.success(request, 'Instructor registered successfully!')
            return redirect('index')
    else:
        form = InstructorRegistrationForm()
    return render(request, 'register_instructor.html', {'form': form})

def register_client(request):
    if request.method == 'POST':
        form = ClientRegistrationForm(request.POST)
        if form.is_valid():
            user = User.objects.create_user(
                username=form.cleaned_data['username'],
                password=form.cleaned_data['password']
            )
            Client.objects.create(
                user=user,
                age=form.cleaned_data['age'],
                guardian=form.cleaned_data['guardian']
            )
            messages.success(request, 'Client registered successfully!')
            return redirect('index')
    else:
        form = ClientRegistrationForm()
    return render(request, 'register_client.html', {'form': form})

def login_view(request):
    if request.method == 'POST':
        username = request.POST['username']
        password = request.POST['password']
        user = authenticate(request, username=username, password=password)
        if user is not None:
            login(request, user)
            messages.success(request, 'Logged in successfully!')
            return redirect('index')
        else:
            messages.error(request, 'Invalid username or password.')
    return render(request, 'login.html')

def book_lesson(request, lesson_id):
    if request.method == 'POST':
        client_name = request.POST['client_name']
        client_age = int(request.POST['client_age'])
        guardian_name = request.POST['guardian_name'] if client_age < 18 else None
        
        lesson = Lesson.objects.get(id=lesson_id)
        if not lesson.availability:
            messages.error(request, 'This lesson is no longer available.')
            return redirect('index')

        client = Client(name=client_name, age=client_age, guardian=guardian_name)
        client.save()

        booking = Booking(client=client, lesson=lesson)
        booking.save()
        
        lesson.availability = False
        lesson.save()

        messages.success(request, 'Lesson booked successfully!')
        return redirect('index')
    return redirect('index')

def home(request):
    return render(request, 'home.html')