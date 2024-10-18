from flask import Flask, render_template, request, redirect, url_for, flash
from flask_sqlalchemy import SQLAlchemy

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///lessons.db'
app.secret_key = 'your_secret_key'  # Required for flash messages
db = SQLAlchemy(app)

# Models
class Instructor(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    phone = db.Column(db.String(15), nullable=False)
    specialization = db.Column(db.String(100), nullable=False)
    cities = db.Column(db.String(200), nullable=False)

class Lesson(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    lesson_type = db.Column(db.String(50), nullable=False)  # Private or Group
    location = db.Column(db.String(100), nullable=False)
    city = db.Column(db.String(100), nullable=False)
    instructor_id = db.Column(db.Integer, db.ForeignKey('instructor.id'), nullable=True)
    schedule = db.Column(db.String(100), nullable=False)
    availability = db.Column(db.Boolean, default=True)

class Client(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    age = db.Column(db.Integer, nullable=False)
    guardian = db.Column(db.String(100), nullable=True)

class Booking(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    client_id = db.Column(db.Integer, db.ForeignKey('client.id'), nullable=False)
    lesson_id = db.Column(db.Integer, db.ForeignKey('lesson.id'), nullable=False)
    is_active = db.Column(db.Boolean, default=True)

# Routes
@app.route('/')
def index():
    lessons = Lesson.query.filter_by(availability=True).all()
    return render_template('index.html', lessons=lessons)

@app.route('/register_instructor', methods=['GET', 'POST'])
def register_instructor():
    if request.method == 'POST':
        name = request.form['name']
        phone = request.form['phone']
        specialization = request.form['specialization']
        cities = request.form['cities']
        new_instructor = Instructor(name=name, phone=phone, specialization=specialization, cities=cities)
        try:
            db.session.add(new_instructor)
            db.session.commit()
            flash('Instructor registered successfully!', 'success')
        except Exception as e:
            db.session.rollback()
            flash(f'Error registering instructor: {e}', 'danger')
        return redirect(url_for('index'))
    return render_template('register_instructor.html')

@app.route('/book_lesson/<int:lesson_id>', methods=['POST'])
def book_lesson(lesson_id):
    client_name = request.form['client_name']
    client_age = int(request.form['client_age'])
    guardian_name = request.form['guardian_name'] if client_age < 18 else None
    client = Client(name=client_name, age=client_age, guardian=guardian_name)

    try:
        db.session.add(client)
        db.session.commit()
        
        booking = Booking(client_id=client.id, lesson_id=lesson_id)
        db.session.add(booking)
        
        lesson = Lesson.query.get(lesson_id)
        if lesson:
            lesson.availability = False
            db.session.commit()
            flash('Lesson booked successfully!', 'success')
        else:
            flash('Lesson not found.', 'danger')

    except Exception as e:
        db.session.rollback()
        flash(f'Error booking lesson: {e}', 'danger')

    return redirect(url_for('index'))

if __name__ == '__main__':
    db.create_all()  # Ensure the database and tables are created
    app.run(debug=True)
