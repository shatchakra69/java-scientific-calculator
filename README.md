# Scientific Calculator

A desktop scientific calculator I built for my Backend Programming final project.

## About

I built this scientific calculator in Java Swing as my Backend Programming final project.
It goes from basic arithmetic all the way to trig, logarithms, powers, roots and factorials.
I kept the main screen clean and tucked the scientific functions behind two buttons — Trig and Math — so it never feels crowded.
Instead of working one step at a time, it reads and solves whole expressions like `sin(30) + 2 × √(9)`.
I designed it around OOP principles, so every operation is its own class and the maths is fully separated from the interface.
The goal was something simple to use but solid underneath.

## Features

- Basic arithmetic with proper operator precedence and parentheses
- **Trig** menu — `sin`, `cos`, `tan` and their inverses, `sinh`/`cosh`/`tanh` and their inverses, plus a Deg/Rad toggle
- **Math** menu — `x²`, `x³`, `xʸ`, `√`, `∛`, yth root, `1/x`, `n!`, `%`, `log`, `ln`, `eˣ`, `10ˣ`, `EE`, `π`, `e` and a random number
- Memory keys — `MS`, `MR`, `MC`, `M+`, `M−`
- Calculation history — double-click any entry to reuse its result
- Full keyboard support — type the expression, `Enter` to solve, `Esc` to clear
- Animated buttons and a live preview of the result as you type
- Clear, friendly error messages (*Division by Zero*, *Invalid Input*, and more)

## Tech Stack

- **Frontend (UI):** Java Swing — custom-painted animated buttons and pop-up function panels
- **Backend (logic):** Core Java — an expression evaluator (tokenizer → shunting-yard → RPN), an OOP operation hierarchy, and a custom exception layer
- **Language:** Java (JDK 8+)
- **Dev tools:** `javac`, Git & GitHub, and shell scripts (`run.sh`, `test.sh`) for building, running and testing

## Run it

```bash
./run.sh     # compiles and launches the calculator
./test.sh    # runs the test suite (75 checks, no GUI)
```

## Author

Built entirely by me — **Shat Chakra Pawar Amgothu**.

## Contact

- [Email](mailto:shatchakra69@gmail.com)
- [LinkedIn](https://www.linkedin.com/in/shat-chakra-pawar-amgothu-a6921a2b4/)
