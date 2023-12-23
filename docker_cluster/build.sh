project_dir="$(dirname "$PWD")"
echo "${project_dir}"

if [ -d "${project_dir}/docker_cluster/target" ]; then rm -Rf "${project_dir}/docker_cluster/target"; fi

mvn -f ../pom.xml  clean package -Dmaven.test.skip

cp -r "${project_dir}/target" "${project_dir}/docker_cluster/target"

docker build -t keks/tourist-bot:1.0 -f Dockerfile .

if [ -d "${project_dir}/docker_cluster/target" ]; then rm -Rf "${project_dir}/docker_cluster/target"; fi

