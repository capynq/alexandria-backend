# API examples

Start with an empty database and the service on `localhost:8080`. Commands below use `jq` to retain IDs returned by the server. The API uses snake_case JSON keys.

```sh
base=http://localhost:8080
group=$(curl -fsS -H 'Content-Type: application/json' -d '{"name":"A1"}' "$base/api/groups/create" | jq -r .id)
subject=$(curl -fsS -H 'Content-Type: application/json' -d '{"subject_name":"Mathematics"}' "$base/api/subjects/create" | jq -r .id)
teacher=$(curl -fsS -H 'Content-Type: application/json' -d '{"full_name":"Teacher Example","login":"teacher1","password":"secret123","is_admin":false}' "$base/api/teachers/create" | jq -r .id)
student=$(curl -fsS -H 'Content-Type: application/json' -d '{"full_name":"Student Example","login":"student1","password":"secret123","group_name":"A1"}' "$base/api/students/create" | jq -r .id)
homework=$(curl -fsS -H 'Content-Type: application/json' -d "{\"subject_id\":$subject,\"group_id\":$group,\"teacher_id\":$teacher,\"title\":\"Algebra\",\"description\":\"Complete exercises 1-3\",\"deadline\":\"2030-01-01T12:00:00Z\"}" "$base/api/homework" | jq -r .id)
curl -fsS "$base/api/homework/search/id/$homework" | jq
curl -fsS "$base/api/homework-status/student/$student" | jq
curl -fsS -X PUT "$base/api/homework-status/mark/$homework/$student" | jq
curl -fsS -X PUT "$base/api/homework-status/mark/$homework/$student" | jq
```

The second mark request returns the existing completed status. Use a different database or unique names and logins to repeat the setup. Responses do not include passwords. Login accepts `{"login":"student1","password":"secret123"}` at `POST /api/students/login` or the analogous teacher route; it does not issue a token.
