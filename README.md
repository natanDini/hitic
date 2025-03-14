<h1 align="center">HITIC</h1>
<h2 align="center">Health Information Technology Iniciação Científica</h2>

<p align="center">
  <img src="/images/Medical_Robot_Logo.png" width="50%" />
</p>

## Sobre o Projeto
O **HITIC** (Health Information Technology) é um projeto de pesquisa científica no âmbito da Iniciação Científica, focado no estudo e desenvolvimento de soluções que utilizam Inteligência Artificial (IA) na área da saúde. O objetivo é explorar como tecnologias emergentes podem melhorar o atendimento médico, otimizar processos hospitalares e proporcionar uma assistência mais eficaz aos pacientes.

## Tecnologias Utilizadas
O projeto adota um ecossistema de tecnologias modernas para sua implementação:

- **Frontend:** Angular
- **Backend:** Java com Spring Boot
- **Inteligência Artificial & Processamento de Dados:** Python
- **Banco de Dados:** PostgreSQL
- **Serviço de OCR:** API do OCR Space

## Configuração do Projeto

### Banco de Dados
Para rodar o backend, é necessário configurar a tabela `parameter` com as chaves da API de OCR e da OpenAI. Execute as seguintes queries no PostgreSQL:

```sql
INSERT INTO public."parameter" (param_key, value)
VALUES ('OCR_API', 'sua-chave-api-ocr-space');

INSERT INTO public."parameter" (param_key, value)
VALUES ('OPENAI_API', 'sua-chave-api-openai');
```

### Executando o Frontend
Para rodar o frontend Angular, utilize o seguinte comando:

```sh
ng serve
```

Acesse o frontend através da URL:

```
http://localhost:4200
```

### Executando o Backend
O backend Spring Boot está configurado com o **context path**:

```
/hitic/api
```

A documentação da API (Swagger) pode ser acessada pelo seguinte link:

```
http://localhost:8080/hitic/api/swagger-ui/index.html
```

## Objetivos do Projeto
1. Pesquisar e aplicar modelos de IA na análise de dados médicos.
2. Desenvolver uma plataforma para suporte à tomada de decisão médica.
3. Integrar tecnologias modernas para otimização do fluxo de informações em unidades de saúde.
4. Avaliar a viabilidade da implementação dessas soluções no contexto hospitalar.