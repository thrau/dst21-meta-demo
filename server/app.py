from typing import List, Dict

from flask import Flask, json, request

app = Flask(__name__)


class User:
    username: str
    email: str
    firstName: str
    lastName: str

    def __init__(self, username=None, email=None, first_name=None, last_name=None) -> None:
        super().__init__()
        self.username = username
        self.email = email
        self.firstName = first_name
        self.lastName = last_name


class UserDirectory:
    data: Dict[str, User]

    def __init__(self) -> None:
        super().__init__()
        self.data = dict()

    def list_users(self) -> List[User]:
        return list(self.data.values())

    def add_user(self, user: User):
        if not user.username:
            raise ValueError('user needs a username')
        if user.username in self.data:
            raise ValueError('user %s already exists' % user.username)

        self.data[user.username] = user


@app.route('/')
def hello_world():
    return 'Hello, World!'


@app.route('/user', methods=['POST'])
def add_user():
    doc = request.json

    user = User(
        username=doc.get('username'),
        email=doc.get('email'),
        first_name=doc.get('firstName'),
        last_name=doc.get('lastName')
    )

    if not user.username:
        return app.response_class(response='no username set', status=400)

    if user.username in directory.data:
        return app.response_class(response=f'user {user.username} already exists', status=409)

    directory.add_user(user)
    return app.response_class(status=201)


@app.route('/user/<username>')
def get_user(username):
    user = directory.data.get(username)
    if user:
        return json.jsonify(user.__dict__)

    return app.response_class(status=404)


@app.route('/users')
def find_user():
    email = request.args.get('email')
    first_name = request.args.get('firstName')
    last_name = request.args.get('lastName')

    result = list()

    for user in directory.list_users():
        if email and user.email != email:
            continue
        if first_name and user.firstName != first_name:
            continue
        if last_name and user.lastName != last_name:
            continue

        result.append(user.__dict__)

    return json.jsonify(result)


directory = UserDirectory()

directory.add_user(User("trillian", "trillian@earth.planet", "Tricia", "McMillan"))
directory.add_user(User("arthur", "arthur@earth.planet", "Arthur", "Dent"))
directory.add_user(User("zaphod", "zaphod@univer.ze", "Zaphod", "Beeblebrox"))
directory.add_user(User("dadams", "douglas@earth.planet", "Douglas", "Adams"))
directory.add_user(User("hal", "arthur@clarkefoundation.org", "Arthur", "Clarke"))
