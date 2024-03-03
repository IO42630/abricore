import mysql.connector
import plotly.graph_objs as go
import plotly.offline as pyo

# Connect to the MariaDB database
mydb = mysql.connector.connect(
    host="localhost",
    port="24001",
    user="user",
    password="",
    database="ABRICORE"
)

# Create a cursor object
mycursor = mydb.cursor()

# Execute a SELECT statement to retrieve data from the table
mycursor.execute("SELECT instant, traded_price FROM t_snapshot WHERE asset = 'XAGUSD' AND instant > '2023-04-18 09:00' AND instant < '2023-04-18 19:00'")

# Fetch all the rows from the result set
rows = mycursor.fetchall()

# Create a list of x values and y values
x_values = [row[0] for row in rows]
y_values = [row[1] for row in rows]

# Create a trace for the scatter plot
trace = go.Scatter(
    x=x_values,
    y=y_values,
    mode='lines'
)

# Create a list of traces
data = [trace]

# Create a layout for the plot
layout = go.Layout(
    title='XAGUSD',
    xaxis=dict(title='instant'),
    yaxis=dict(title='traded_price')
)

# Create a figure object
fig = go.Figure(data=data, layout=layout)

fig.update_layout(xaxis=dict(title='Column1', rangeslider=dict(visible=True)),
                  yaxis=dict(title='Column2', autorange=True, type='linear'))

# Save the plot to an HTML file
pyo.plot(fig, filename='plot.html')
