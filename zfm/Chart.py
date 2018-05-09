#!/usr/bin/python
#encoding=utf8
import plotly.plotly
import plotly.offline as pltoff
import plotly.graph_objs as go
def bar_charts(name):
  '''
  绘制柱状图
  '''
  dataset = {'x':['Windows', 'Linux', 'Unix', 'MacOS'],
        'y1':[45, 26, 37, 13],
        'y2':[19, 27, 33, 21]}
  data_g = []
  tr_y1 = go.Bar(
    x = dataset['x'],
    y = dataset['y1'],
    name = 'v1'
  )
  data_g.append(tr_y1)
 
  tr_y2 = go.Bar(
    x = dataset['x'],
    y = dataset['y2'],
    name = 'v2'
  )
  data_g.append(tr_y2)
  layout = go.Layout(title="bar charts", xaxis={'title':'x'}, yaxis={'title':'value'})
  fig = go.Figure(data=data_g, layout=layout)
  plotly.offline.plot(fig, filename=name)

if __name__ == '__main__':
	bar_charts("Chart.html")
