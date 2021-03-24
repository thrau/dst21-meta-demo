class MyClass:

    def __init__(self) -> None:
        super().__init__()
        self.my_field = "something"
        self.my_field = 1


def logger(fn):
    print("1: call logger")

    def log_call(*args, **kwargs):
        print("3: calling ", fn.__name__)
        return fn(*args, **kwargs)

    return log_call


def logger_with_prefix(prefix):
    return logger


@logger
def get_my_name(suffix):
    return "thomas " + suffix


@logger
def combine(a, b, c):
    return a + b + c


def main():
    print("2: call main")
    print(get_my_name("!"))
    print(get_my_name("!"))
    print(get_my_name("!"))
    print(get_my_name("!"))
    print(get_my_name("!"))
    print(get_my_name("!"))
    print(get_my_name("!"))
    print(combine(c="c", a="1", b="2"))


if __name__ == '__main__':
    main()
