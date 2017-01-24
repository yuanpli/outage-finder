<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Outage Risk Report</title>
    <style>
    table, th, td {
        border: 1px solid black;
        text-align: left;
    }
    </style>
</head>
<body style="width:100%">
<table>
  <col width="5%">
  <col width="20%">
  <col width="5%">
  <col width="5%">
  <col width="5%">
  <col width="30%">
  <col width="20%">
  <col width="10%">
    <tr>
        <td>ID</td>
        <td>ClassName</td>
        <td>Line</td>
        <td>Type</td>
        <td>Level</td>
        <td>Description</td>
        <td>Sample</td>
        <td>Path</td>
    </tr>
    <#list risks as risk>
        <tr>
        <td>${risk?counter}</td>
        <td>${risk.className}</td>
        <td>${risk.row}</td>
        <td>${risk.type.id}</td>
        <td>${risk.type.riskLevel}</td>
        <td>${risk.type.description}</td>
        <td>${risk.sample}</td>
        <td>${risk.path}</td>
        <tr>
    </#list>
</table>
</body>
</html>