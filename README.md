Assuming a linux environment

# Prerequisites

Python

### Installation

To see which version of Python 3 you have installed, open a command prompt and run
  ```sh
  $ python3 --version
  ```
If you are using Ubuntu 16.10 or newer, then you can easily install Python 3.6 with the following commands:
  ```sh
  $ sudo apt-get update
  $ sudo apt-get install python3.6
  ```
If you’re using another version of Ubuntu (e.g. the latest LTS release) or you want to use a more current Python, we recommend using the deadsnakes PPA to install Python 3.8:
  ```sh
  $ sudo apt-get install software-properties-common
  $ sudo add-apt-repository ppa:deadsnakes/ppa
  $ sudo apt-get update
  $ sudo apt-get install python3.8
  ```
If you are using other Linux distribution, chances are you already have Python 3 pre-installed as well. If not, use your distribution’s package manager. For example on Fedora, you would use dnf:
  ```sh
  $ sudo dnf install python3
  ```

# Run Server and Client

Server must be initialized before client

- Run Server
  python3 server.py [port]
- Run Client
  python3 client.py [port]

# Program Documentation

- Program is implemented using 3 Way Handshake
- Frame class is an object template for refactoring every messages that needs to be transferred between peers
- decodeMessage() function can be used to accept the incoming bit messages and turn it into a Frame object.
